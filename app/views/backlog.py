from flask import Blueprint, render_template, request, jsonify, flash, redirect, url_for
from flask_login import login_required, current_user
from app.models.project import Project
from app.models.story import Story
from app.models.sprint import Sprint
from app.models.user import User
from app import db
from datetime import datetime, timedelta
from sqlalchemy import case

bp = Blueprint("backlog", __name__)


@bp.route("/backlog")
@bp.route("/backlog/<int:project_id>")
@login_required
def index(project_id=None):
    """バックログページを表示"""
    projects = Project.query.all()
    current_project = None
    users = User.query.filter_by(is_active=True).all()

    if project_id:
        current_project = Project.query.get_or_404(project_id)
    elif projects:
        current_project = projects[0]

    # 全てのスプリントを取得
    sprints = []
    backlog_stories = []
    active_sprint = None
    if current_project:
        # アクティブなスプリントを取得
        active_sprint = Sprint.query.filter_by(
            project_id=current_project.id,
            status='active'
        ).first()

        # スプリントを番号順に取得
        sprints = Sprint.query.filter_by(
            project_id=current_project.id
        ).order_by(
            Sprint.name.asc()  # Sprint 1, Sprint 2, ... の順
        ).all()

        # バックログのストーリーを取得
        backlog_stories = Story.query.filter_by(
            project_id=current_project.id,
            sprint_id=None
        ).order_by(Story.priority.desc()).all()

        # 各スプリントに追加情報を設定
        for sprint in sprints:
            if sprint.status == 'planning':
                # このスプリントが開始可能かどうかを判定
                can_start = not active_sprint  # アクティブなスプリントがない
                if can_start:
                    # 番号が小さいプランニング中のスプリントがないことを確認
                    for s in sprints:
                        if s.status == 'planning' and s.name < sprint.name:
                            can_start = False
                            break
                sprint.can_start = can_start

    return render_template(
        "backlog/index.html",
        title="バックログ",
        projects=projects,
        current_project=current_project,
        sprints=sprints,
        backlog_stories=backlog_stories,
        active_sprint=active_sprint,
        users=users
    )


@bp.route("/backlog/story/create", methods=["POST"])
@login_required
def create_story():
    """ストーリーを作成"""
    if request.is_json:
        data = request.get_json()
        project_id = data.get('project_id')
        if not project_id:
            return jsonify({'status': 'error', 'message': 'プロジェクトが選択されていません'}), 400

        story = Story(
            title=data.get('title'),
            description=data.get('description'),
            story_points=data.get('story_points', 0),
            project_id=project_id,
            assignee_id=data.get('assignee_id') if data.get('assignee_id') else None,
            priority=data.get('priority', 0)
        )
        
        try:
            db.session.add(story)
            db.session.commit()
            return jsonify({'status': 'success'})
        except Exception as e:
            db.session.rollback()
            return jsonify({'status': 'error', 'message': str(e)}), 400
    else:
        project_id = request.form.get('project_id', type=int)
        if not project_id:
            flash('プロジェクトが選択されていません', 'error')
            return redirect(url_for('backlog.index'))

        story = Story(
            title=request.form.get('title'),
            description=request.form.get('description'),
            story_points=request.form.get('story_points', 0, type=int),
            project_id=project_id,
            assignee_id=request.form.get('assignee_id', type=int),
            priority=request.form.get('priority', 0, type=int)
        )
        
        db.session.add(story)
        db.session.commit()
        
        flash('ストーリーが作成されました', 'success')
        return redirect(url_for('backlog.index', project_id=project_id))


@bp.route("/backlog/story/<int:story_id>/update", methods=["POST"])
@login_required
def update_story(story_id):
    """ストーリーを更新"""
    story = Story.query.get_or_404(story_id)
    
    if request.is_json:
        data = request.get_json()
        story.title = data.get('title', story.title)
        story.description = data.get('description', story.description)
        story.story_points = data.get('story_points', story.story_points)
        story.assignee_id = data.get('assignee_id') if data.get('assignee_id') else None
        story.priority = data.get('priority', 0)
    else:
        story.title = request.form.get('title', story.title)
        story.description = request.form.get('description', story.description)
        story.story_points = request.form.get('story_points', story.story_points, type=int)
        story.assignee_id = request.form.get('assignee_id', type=int)
        story.priority = request.form.get('priority', 0, type=int)
    
    try:
        db.session.commit()
        return jsonify({'status': 'success'})
    except Exception as e:
        db.session.rollback()
        return jsonify({'status': 'error', 'message': str(e)}), 400


@bp.route("/backlog/story/<int:story_id>/move", methods=["POST"])
@login_required
def move_story(story_id):
    """ストーリーの移動（スプリントとバックログ間）"""
    story = Story.query.get_or_404(story_id)
    sprint_id = request.form.get('sprint_id', type=int)
    
    if sprint_id:
        story.sprint_id = sprint_id
        story.status = Story.KANBAN_TODO
    else:
        story.sprint_id = None
        story.status = Story.KANBAN_TODO
    
    db.session.commit()
    return jsonify({'status': 'success'})


@bp.route("/backlog/sprint/start", methods=["POST"])
@login_required
def start_sprint():
    """スプリントを開始"""
    sprint_id = request.form.get('sprint_id', type=int)
    if not sprint_id:
        flash('スプリントが選択されていません', 'error')
        return redirect(url_for('backlog.index'))

    sprint = Sprint.query.get_or_404(sprint_id)
    
    # スプリント期間を設定（デフォルト2週間）
    sprint.start_date = datetime.utcnow()
    sprint.end_date = sprint.start_date + timedelta(days=14)
    sprint.status = 'active'
    
    db.session.commit()
    
    flash('スプリントが開始されました', 'success')
    return redirect(url_for('backlog.index', project_id=sprint.project_id))


@bp.route("/backlog/sprint/create", methods=["POST"])
@login_required
def create_sprint():
    """新しいスプリントを作成"""
    project_id = request.form.get('project_id', type=int)
    if not project_id:
        flash('プロジェクトが選択されていません', 'error')
        return redirect(url_for('backlog.index'))

    project = Project.query.get_or_404(project_id)
    
    # スプリント名を生成（Sprint n の形）
    sprint_number = len(project.sprints) + 1
    name = f"Sprint {sprint_number}"
    
    # 日付文字列をdatetimeオブジェクトに変換
    start_date_str = request.form.get('start_date')
    end_date_str = request.form.get('end_date')
    
    start_date = datetime.strptime(start_date_str, '%Y-%m-%d') if start_date_str else None
    end_date = datetime.strptime(end_date_str, '%Y-%m-%d') if end_date_str else None
    
    sprint = Sprint(
        name=name,
        project_id=project_id,
        status='planning',
        start_date=start_date,
        end_date=end_date,
        goal=request.form.get('goal')
    )
    
    db.session.add(sprint)
    db.session.commit()
    
    flash('新しいスプリントが作成されました', 'success')
    return redirect(url_for('backlog.index', project_id=project_id))


@bp.route('/api/sprints/create', methods=['POST'])
@login_required
def create_sprint_api():
    data = request.get_json()
    name = data.get('name', '')
    goal = data.get('goal', '')

    if not name:
        return jsonify({'error': 'スプリント名は必須です'}), 400

    # 新しい Sprint を作成
    sprint = Sprint(
        name=name,
        goal=goal,
        status='planning'
    )
    db.session.add(sprint)
    db.session.commit()

    return jsonify({
        'id': sprint.id,
        'name': sprint.name,
        'goal': sprint.goal,
        'status': sprint.status
    })


@bp.route('/api/sprints/<int:sprint_id>/start', methods=['POST'])
@login_required
def start_sprint_api(sprint_id):
    sprint = Sprint.query.get_or_404(sprint_id)
    
    # 检查是否已经有进行中的 Sprint
    active_sprint = Sprint.query.filter_by(
        project_id=sprint.project_id,
        status='active'
    ).first()
    
    if active_sprint and active_sprint.id != sprint.id:
        return jsonify({'status': 'error', 'error': '他のスプリントが進行中です'}), 400
    
    if sprint.status != 'planning':
        return jsonify({'status': 'error', 'error': 'このスプリントは開始できません'}), 400

    # 检查是否有编号更小的未完成 Sprint
    earlier_sprints = Sprint.query.filter(
        Sprint.project_id == sprint.project_id,
        Sprint.name < sprint.name,
        Sprint.status == 'planning'
    ).first()
    
    if earlier_sprints:
        return jsonify({'status': 'error', 'error': '前のスプリントを先に開始してください'}), 400

    sprint.status = 'active'
    sprint.start_date = datetime.now()
    sprint.end_date = sprint.start_date + timedelta(days=14)
    db.session.commit()

    return jsonify({
        'status': 'success',
        'data': {
            'id': sprint.id,
            'status': sprint.status
        }
    })


@bp.route('/api/sprints/<int:sprint_id>/complete', methods=['POST'])
@login_required
def complete_sprint_api(sprint_id):
    sprint = Sprint.query.get_or_404(sprint_id)
    
    if sprint.status != 'active':
        return jsonify({'status': 'error', 'error': 'このスプリントは完了できません'}), 400

    sprint.status = 'completed'
    sprint.completed_at = datetime.now()
    db.session.commit()

    return jsonify({
        'status': 'success',
        'data': {
            'id': sprint.id,
            'status': sprint.status
        }
    })
