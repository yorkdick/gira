from flask import Blueprint, render_template, request, jsonify
from flask_login import login_required
from app.models.project import Project
from app.models.sprint import Sprint
from app.models.story import Story
from app.extensions import db

bp = Blueprint('kanban', __name__)

@bp.route('/')
@bp.route('/<int:project_id>')
@login_required
def index(project_id=None):
    projects = Project.query.all()
    current_project = None
    active_sprint = None
    todo_stories = []
    doing_stories = []
    done_stories = []

    if project_id:
        current_project = Project.query.get_or_404(project_id)
        active_sprint = Sprint.query.filter_by(
            project_id=current_project.id,
            status='active'
        ).first()
        
        if active_sprint:
            todo_stories = Story.query.filter_by(
                sprint_id=active_sprint.id,
                status=Story.KANBAN_TODO
            ).all()
            
            doing_stories = Story.query.filter_by(
                sprint_id=active_sprint.id,
                status=Story.KANBAN_DOING
            ).all()
            
            done_stories = Story.query.filter_by(
                sprint_id=active_sprint.id,
                status=Story.KANBAN_DONE
            ).all()
    elif projects:
        current_project = projects[0]
        active_sprint = Sprint.query.filter_by(
            project_id=current_project.id,
            status='active'
        ).first()
        
        if active_sprint:
            todo_stories = Story.query.filter_by(
                sprint_id=active_sprint.id,
                status=Story.KANBAN_TODO
            ).all()
            
            doing_stories = Story.query.filter_by(
                sprint_id=active_sprint.id,
                status=Story.KANBAN_DOING
            ).all()
            
            done_stories = Story.query.filter_by(
                sprint_id=active_sprint.id,
                status=Story.KANBAN_DONE
            ).all()

    return render_template(
        'kanban/index.html',
        title='Active Sprint',
        projects=projects,
        current_project=current_project,
        active_sprint=active_sprint,
        todo_stories=todo_stories,
        doing_stories=doing_stories,
        done_stories=done_stories
    )

@bp.route('/api/stories/<int:story_id>/status', methods=['PUT'])
@login_required
def update_story_status(story_id):
    story = Story.query.get_or_404(story_id)
    data = request.get_json()
    new_status = data.get('status')
    
    if new_status not in [Story.KANBAN_TODO, Story.KANBAN_DOING, Story.KANBAN_DONE]:
        return jsonify({
            'status': 'error',
            'message': 'Invalid status value'
        }), 400
    
    story.status = new_status
    
    try:
        db.session.commit()
        return jsonify({'status': 'success'})
    except Exception as e:
        db.session.rollback()
        return jsonify({
            'status': 'error',
            'message': str(e)
        }), 500