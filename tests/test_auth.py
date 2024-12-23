import unittest
from app import create_app, db
from app.models.user import User
from flask import url_for

class TestAuthViews(unittest.TestCase):
    def setUp(self):
        self.app = create_app('testing')
        self.client = self.app.test_client()
        with self.app.app_context():
            db.create_all()
            user = User(username='testuser')
            user.set_password('testpass')
            db.session.add(user)
            db.session.commit()

    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()

    def test_login_success(self):
        response = self.client.post(url_for('auth.login'), data={
            'username': 'testuser',
            'password': 'testpass'
        }, follow_redirects=True)
        self.assertIn('ログインに成功しました', response.data)

    def test_login_wrong_password(self):
        response = self.client.post(url_for('auth.login'), data={
            'username': 'testuser',
            'password': 'wrongpass'
        }, follow_redirects=True)
        self.assertIn('ユーザー名またはパスワードが正しくありません', response.data)

    def test_logout(self):
        self.client.post(url_for('auth.login'), data={
            'username': 'testuser',
            'password': 'testpass'
        }, follow_redirects=True)
        response = self.client.get(url_for('auth.logout'), follow_redirects=True)
        self.assertIn('ログアウトしました', response.data)

if __name__ == '__main__':
    unittest.main() 