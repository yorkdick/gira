import pytest
from flask import g, session
from app.models.user import User

def test_login_page(client):
    """ログインページの表示テスト"""
    response = client.get('/login')
    assert response.status_code == 200
    assert b'\xe3\x83\xad\xe3\x82\xb0\xe3\x82\xa4\xe3\x83\xb3' in response.data  # 'ログイン'のUTF-8エンコード

def test_valid_login(client, auth):
    """正常なログインのテスト"""
    response = auth.login()
    assert response.headers['Location'] == '/backlog'

def test_invalid_username(client, auth):
    """存在しないユーザー名でのログインテスト"""
    response = auth.login('invalid', 'testpass')
    # リダイレクト後のページを取得
    response = client.get('/login')
    assert b'\xe3\x83\xa6\xe3\x83\xbc\xe3\x82\xb6\xe3\x83\xbc\xe5\x90\x8d\xe3\x81\xbe\xe3\x81\x9f\xe3\x81\xaf\xe3\x83\x91\xe3\x82\xb9\xe3\x83\xaf\xe3\x83\xbc\xe3\x83\x89\xe3\x81\x8c\xe6\xad\xa3\xe3\x81\x97\xe3\x81\x8f\xe3\x81\x82\xe3\x82\x8a\xe3\x81\xbe\xe3\x81\x9b\xe3\x82\x93' in response.data

def test_invalid_password(client, auth):
    """誤ったパスワードでのログインテスト"""
    response = auth.login('testuser', 'wrongpass')
    # リダイレクト後のページを取得
    response = client.get('/login')
    assert b'\xe3\x83\xa6\xe3\x83\xbc\xe3\x82\xb6\xe3\x83\xbc\xe5\x90\x8d\xe3\x81\xbe\xe3\x81\x9f\xe3\x81\xaf\xe3\x83\x91\xe3\x82\xb9\xe3\x83\xaf\xe3\x83\xbc\xe3\x83\x89\xe3\x81\x8c\xe6\xad\xa3\xe3\x81\x97\xe3\x81\x8f\xe3\x81\x82\xe3\x82\x8a\xe3\x81\xbe\xe3\x81\x9b\xe3\x82\x93' in response.data

def test_empty_username(client):
    """空のユーザー名でのログインテスト"""
    response = client.post('/login', data={
        'username': '',
        'password': 'testpass'
    })
    assert b'This field is required.' in response.data

def test_empty_password(client):
    """空のパスワードでのログインテスト"""
    response = client.post('/login', data={
        'username': 'testuser',
        'password': ''
    })
    assert b'This field is required.' in response.data

def test_logout(client, auth):
    """ログアウトのテスト"""
    # まずログイン
    auth.login()
    
    # ログアウトを実行
    response = auth.logout()
    assert response.headers['Location'] == '/login'

def test_login_required(client):
    """保護されたページへのアクセステスト"""
    response = client.get('/backlog')
    assert response.headers['Location'].startswith('/login')

def test_already_logged_in(client, auth):
    """ログイン済みユーザーのログインページアクセステスト"""
    auth.login()
    response = client.get('/login')
    assert response.headers['Location'] == '/backlog' 