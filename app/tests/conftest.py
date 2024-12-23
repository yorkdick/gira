import os
import tempfile
import pytest
from app import create_app, db
from app.models.user import User
from config import Config

class TestConfig(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = 'sqlite:///:memory:'
    WTF_CSRF_ENABLED = False

@pytest.fixture
def app():
    """アプリケーションのテスト用インスタンスを作成"""
    app = create_app(TestConfig)
    
    # テスト用データベースのセットアップ
    with app.app_context():
        db.create_all()
        
        # テストユーザーの作成
        user = User(username='testuser', email='test@example.com')
        user.set_password('testpass')
        db.session.add(user)
        db.session.commit()
    
    yield app
    
    # テスト後のクリーンアップ
    with app.app_context():
        db.session.remove()
        db.drop_all()

@pytest.fixture
def client(app):
    """テスト用クライアント"""
    return app.test_client()

@pytest.fixture
def runner(app):
    """テスト用CLIランナー"""
    return app.test_cli_runner()

@pytest.fixture
def auth(client):
    """認証ヘルパー"""
    class AuthActions:
        def __init__(self, client):
            self._client = client

        def login(self, username='testuser', password='testpass'):
            return self._client.post(
                '/login',
                data={'username': username, 'password': password}
            )

        def logout(self):
            return self._client.get('/logout')

    return AuthActions(client) 