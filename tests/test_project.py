import pytest
import logging
from flask import url_for
from app import create_app, db
from app.models.project import Project
from app.models.user import User
from flask_login import login_user
from flask_login.utils import _get_user
from config import TestConfig

# 设置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@pytest.fixture(scope='session')
def app():
    app = create_app(TestConfig)
    logger.info(f"测试数据库URL: {app.config['SQLALCHEMY_DATABASE_URI']}")
    return app

@pytest.fixture(scope='function')
def _db(app):
    """每个测试函数都使用新的数据库会话"""
    with app.app_context():
        logger.info(f"测试会话数据库URL: {app.config['SQLALCHEMY_DATABASE_URI']}")
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
            owner_id=test_user.id,
            status='active'
        )
        _db.session.add(project)
        _db.session.commit()
        _db.session.refresh(project)
        return project

def test_index(app, auth_client, test_user, test_project):
    """测试项目一览页面"""
    with app.app_context():
        logger.info(f"测试用户访问项目一览页面 - 用户ID: {test_user.id}")
        response = auth_client.get('/projects')
        assert response.status_code == 200
        logger.info("项目一览页面访问成功")

def test_list_projects(app, auth_client, test_user, test_project):
    """测试项目列表API - 基本功能"""
    with app.app_context():
        logger.info(f"测试用户请求项目列表 - 用户ID: {test_user.id}")
        response = auth_client.get('/api/project/list')
        data = response.get_json()
        
        assert response.status_code == 200
        assert 'projects' in data
        assert len(data['projects']) == 1
        logger.info(f"获取到 {len(data['projects'])} 个项目")

def test_list_projects_with_search(app, auth_client, test_user, test_project):
    """测试项目列表API - 搜索功能"""
    with app.app_context():
        # 测试名称搜索
        logger.info("测试按名称搜索项目")
        response = auth_client.get('/api/project/list?name=Test')
        data = response.get_json()
        assert len(data['projects']) == 1
        logger.info(f"按名称'Test'搜索到 {len(data['projects'])} 个项目")

        # 测试状态搜索
        logger.info("测试按状态搜索项目")
        response = auth_client.get('/api/project/list?status=active')
        data = response.get_json()
        assert len(data['projects']) == 1
        logger.info(f"按状态'active'搜索到 {len(data['projects'])} 个项目")

        # 测试组合搜索
        logger.info("测试组合条件搜索项目")
        response = auth_client.get('/api/project/list?name=Test&status=active')
        data = response.get_json()
        assert len(data['projects']) == 1
        logger.info(f"按名称'Test'和状态'active'搜索到 {len(data['projects'])} 个项目")

def test_create_project(app, auth_client, test_user):
    """测试创建项目API"""
    with app.app_context():
        logger.info(f"测试用户创建新项目 - 用户ID: {test_user.id}")
        response = auth_client.post('/api/project/create', json={
            'name': 'New Test Project',
            'key': 'NEW-TEST',
            'description': 'New Test Description',
            'owner_id': test_user.id
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['status'] == 'success'
        assert data['project']['name'] == 'New Test Project'
        logger.info(f"项目创建成功 - ID: {data['project']['id']}")

        # 验证项目是否正确保存到数据库
        project = Project.query.filter_by(name='New Test Project').first()
        assert project is not None
        assert project.description == 'New Test Description'
        assert project.status == 'active'
        assert project.owner_id == test_user.id
        logger.info("数据库验证通过")

def test_update_project(app, auth_client, test_project):
    """测试更新项目API"""
    with app.app_context():
        logger.info(f"测试更新项目 - ID: {test_project.id}")
        response = auth_client.post('/api/project/update', json={
            'id': test_project.id,
            'name': 'Updated Project',
            'description': 'Updated Description',
            'status': 'archived'
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['status'] == 'success'
        logger.info("项目更新成功")

        # 验证更新是否保存到数据库
        updated_project = Project.query.get(test_project.id)
        assert updated_project.name == 'Updated Project'
        assert updated_project.description == 'Updated Description'
        assert updated_project.status == 'archived'
        logger.info("数据库验证通过")

def test_delete_project(app, auth_client, test_project):
    """测试删除项目API"""
    with app.app_context():
        logger.info(f"测试删除项目 - ID: {test_project.id}")
        response = auth_client.post('/api/project/delete', json={
            'id': test_project.id
        })
        assert response.status_code == 200
        data = response.get_json()
        assert data['status'] == 'success'
        logger.info("项目删除成功")

        # 验证项目状态是否更新为deleted
        deleted_project = Project.query.get(test_project.id)
        assert deleted_project.status == 'deleted'
        logger.info("数据库验证通过")

def test_project_detail(app, auth_client, test_project):
    """测试项目详情页面"""
    with app.app_context():
        logger.info(f"测试访问项目详情页面 - ID: {test_project.id}")
        response = auth_client.get(f'/projects/{test_project.id}')
        assert response.status_code == 200
        logger.info("项目详情页面访问成功")

def test_project_to_dict(app, _db, test_project):
    """测试项目的to_dict方法"""
    with app.app_context():
        # 获取项目的字典表示
        project_dict = test_project.to_dict()
        
        # 验证字典中包含所有必要的字段
        assert project_dict['id'] == test_project.id
        assert project_dict['name'] == 'Test Project'
        assert project_dict['key'] == 'TEST'
        assert project_dict['description'] == 'Test Project Description'
        assert project_dict['status'] == 'active'
        assert project_dict['owner_id'] == test_project.owner_id
        assert project_dict['created_at'] is not None
        assert project_dict['updated_at'] is not None

def test_project_list_api(app, auth_client, test_project, test_user):
    """测试项目列表API"""
    with app.app_context():
        # 确保测试项目在数据库中
        db.session.add(test_project)
        db.session.commit()
        
        # 确认测试项目已经在数据库中
        project_in_db = Project.query.get(test_project.id)
        logger.info(f"测试项目在数据库中: {project_in_db is not None}")
        if project_in_db:
            logger.info(f"项目详情: ID={project_in_db.id}, 名称={project_in_db.name}, ��态={project_in_db.status}")
        
        # 确认用户已登录
        logger.info(f"当前用户ID: {test_user.id}")
        
        # 调用项目列表API
        response = auth_client.get('/api/project/list')
        data = response.get_json()
        logger.info(f"API响应: status_code={response.status_code}, data={data}")
        
        # 验证响应
        assert response.status_code == 200
        assert 'projects' in data
        assert len(data['projects']) == 1
        
        project = data['projects'][0]
        assert project['id'] == test_project.id
        assert project['name'] == 'Test Project'
        assert project['key'] == 'TEST'
        assert project['status'] == 'active'