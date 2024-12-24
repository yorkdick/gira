import pytest
from flask import url_for
from app import create_app, db
from app.models.project import Project
from app.models.user import User
from app.models.story import Story
from app.models.sprint import Sprint
from flask_login import login_user
from flask_login.utils import _get_user
from datetime import datetime, timedelta
from config import TestConfig

@pytest.fixture(scope='session')
def app():
    """创建测试应用"""
    app = create_app(TestConfig)
    return app

@pytest.fixture(scope='function')
def _db(app):
    """每个测试函数都使用新的数据库会话"""
    with app.app_context():
        db.create_all()
        yield db
        db.session.remove()
        db.drop_all()

@pytest.fixture(scope='function')
def client(app, _db):
    return app.test_client()

@pytest.fixture(scope='function')
def test_user(app, _db):
    with app.app_context():
        user = User(
            username='testuser',
            email='test@example.com',
            first_name='Test',
            last_name='User',
            avatar_color='#FF0000'
        )
        user.set_password('password')
        _db.session.add(user)
        _db.session.commit()
        _db.session.refresh(user)
        return user

@pytest.fixture(scope='function')
def auth_client(app, client, test_user):
    """认证客户端"""
    with app.test_request_context():
        login_user(test_user)
        with client.session_transaction() as sess:
            sess['_user_id'] = test_user.id
            sess['_fresh'] = True
    return client

@pytest.fixture(scope='function')
def test_project(app, _db, test_user):
    """创建测试项目"""
    with app.app_context():
        project = Project(
            name='Test Project',
            key='TEST',
            description='Test Project Description',
            owner_id=test_user.id
        )
        _db.session.add(project)
        _db.session.commit()
        _db.session.refresh(project)
        return project

@pytest.fixture(scope='function')
def test_sprint(app, _db, test_project):
    """创建测试Sprint"""
    with app.app_context():
        sprint = Sprint(
            name='Test Sprint',
            goal='Test Sprint Goal',
            project_id=test_project.id,
            start_date=datetime.now(),
            end_date=datetime.now() + timedelta(days=14),
            status='active'
        )
        _db.session.add(sprint)
        _db.session.commit()
        _db.session.refresh(sprint)
        return sprint

@pytest.fixture(scope='function')
def test_story(app, _db, test_project, test_sprint, test_user):
    """创建测试Story"""
    with app.app_context():
        story = Story(
            title='Test Story',
            description='Test Story Description',
            project_id=test_project.id,
            sprint_id=test_sprint.id,
            status=Story.KANBAN_TODO,
            story_points=5,
            priority=Story.PRIORITY_MEDIUM,
            assignee_id=test_user.id
        )
        _db.session.add(story)
        _db.session.commit()
        _db.session.refresh(story)
        return story

def test_kanban_index_without_project(app, auth_client, test_user):
    """测试未选择项目时的看板页面"""
    with app.app_context():
        db.session.add(test_user)
        response = auth_client.get('/kanban/')
        assert response.status_code == 200

def test_kanban_index_with_project(app, auth_client, test_project, test_user):
    """测试选择项目时的看板页面"""
    with app.app_context():
        db.session.add(test_user)
        db.session.add(test_project)
        response = auth_client.get(f'/kanban/{test_project.id}')
        assert response.status_code == 200

def test_update_story_status(app, auth_client, test_story, test_user):
    """测试更新Story状态"""
    with app.app_context():
        db.session.add(test_user)
        db.session.add(test_story)
        
        # 将Story状态从TODO更新为DOING
        response = auth_client.put(f'/kanban/api/stories/{test_story.id}/status', json={
            'status': Story.KANBAN_DOING
        })
        assert response.status_code == 200
        
        # 验证Story状态已更新
        story = Story.query.get(test_story.id)
        assert story.status == Story.KANBAN_DOING

def test_update_story_status_invalid(app, auth_client, test_story, test_user):
    """测试使用无效状态更新Story"""
    with app.app_context():
        db.session.add(test_user)
        db.session.add(test_story)
        
        # 尝试将Story更新为无效状态
        response = auth_client.put(f'/kanban/api/stories/{test_story.id}/status', json={
            'status': 'invalid_status'
        })
        assert response.status_code == 400