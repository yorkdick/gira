import click
from flask.cli import with_appcontext
from app import db
from app.models.user import User
from app.models.project import Project
from app.models.story import Story
from app.models.sprint import Sprint
import random

@click.command('init-db')
@with_appcontext
def init_db_command():
    """Clear the existing data and create new tables."""
    db.drop_all()
    db.create_all()
    click.echo('Initialized the database.')

@click.command('create-test-data')
@with_appcontext
def create_test_data():
    """Create test data."""
    # 创建用户
    users_data = [
        {
            'username': 'yamada',
            'email': 'yamada@example.com',
            'password': 'password',
            'first_name': 'Taro',
            'last_name': 'Yamada',
            'avatar_color': '#2D8738'
        },
        {
            'username': 'tanaka',
            'email': 'tanaka@example.com',
            'password': 'password',
            'first_name': 'Hanako',
            'last_name': 'Tanaka',
            'avatar_color': '#0052CC'
        },
        {
            'username': 'suzuki',
            'email': 'suzuki@example.com',
            'password': 'password',
            'first_name': 'Ichiro',
            'last_name': 'Suzuki',
            'avatar_color': '#CD1F1F'
        }
    ]

    for user_data in users_data:
        user = User.query.filter_by(email=user_data['email']).first()
        if not user:
            user = User(
                username=user_data['username'],
                email=user_data['email'],
                first_name=user_data['first_name'],
                last_name=user_data['last_name'],
                avatar_color=user_data['avatar_color']
            )
            user.set_password(user_data['password'])
            db.session.add(user)

    # 创建项目
    project = Project.query.filter_by(key='DEMO').first()
    if not project:
        project = Project(
            key='DEMO',
            name='Demo Project'
        )
        db.session.add(project)
        db.session.commit()

    # 创建 Sprint
    sprint_names = ['Sprint 1', 'Sprint 2', 'Sprint 3']
    sprints = []
    for name in sprint_names:
        sprint = Sprint.query.filter_by(name=name, project_id=project.id).first()
        if not sprint:
            sprint = Sprint(
                name=name,
                project_id=project.id,
                status='planning'
            )
            db.session.add(sprint)
            sprints.append(sprint)
    
    db.session.commit()

    # 创建 Story
    story_titles = [
        'ユーザー登録機能の実装',
        'ログイン機能の実装',
        'プロフィール編集機能の実装',
        'パスワードリセット機能の実装',
        'メール通知機能の実装',
        'ダッシュボード画面の作成',
        '管理者画面の作成',
        'レポート機能の実装',
        'API認証の実装',
        'パフォーマンス最適化'
    ]

    priorities = [Story.PRIORITY_NONE, Story.PRIORITY_LOW, Story.PRIORITY_MEDIUM, 
                 Story.PRIORITY_HIGH, Story.PRIORITY_HIGHEST]
    users = User.query.all()

    for i, title in enumerate(story_titles):
        story = Story.query.filter_by(title=title, project_id=project.id).first()
        if not story:
            story = Story(
                title=title,
                description=f'{title}の詳細説明です。',
                project_id=project.id,
                story_points=random.randint(1, 8),
                priority=random.choice(priorities),
                assignee=random.choice(users) if random.random() > 0.3 else None
            )
            if i < 6:  # 前6个故事分配到Sprint中
                story.sprint_id = sprints[i // 2].id if sprints else None
                story.status = 'sprint'
            db.session.add(story)

    db.session.commit()
    click.echo('Created test data.') 

@click.command('create-test-projects')
def create_test_projects():
    """创建测试项目数据"""
    projects = [
        {
            'name': 'テストプロジェクト1',
            'key': 'PRJ-0001',
            'description': 'テスト用プロジェクト1の説明文',
            'status': 'active'
        },
        {
            'name': 'テストプロジェクト2',
            'key': 'PRJ-0002',
            'description': 'テスト用プロジェクト2の説明文',
            'status': 'active'
        },
        {
            'name': 'アーカイブプロジェクト',
            'key': 'PRJ-0003',
            'description': 'アーカイブされたプロジェクト',
            'status': 'archived'
        }
    ]
    
    for project_data in projects:
        project = Project(**project_data)
        db.session.add(project)
    
    db.session.commit()
    print('テストプロジェクトを作成しました') 