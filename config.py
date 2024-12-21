import os
from datetime import timedelta

class Config:
    # 基本設定
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'dev-secret-key'
    
    # データベース設定
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL') or \
        'sqlite:///' + os.path.join(os.path.abspath(os.path.dirname(__file__)), 'app.db')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    
    # セッション設定
    PERMANENT_SESSION_LIFETIME = timedelta(minutes=60)
    
    # ログ設定
    LOG_TYPE = os.environ.get('LOG_TYPE', 'stream')  # stream or file
    LOG_LEVEL = os.environ.get('LOG_LEVEL', 'INFO')
    LOG_FILE = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'logs', 'app.log')
    LOG_FORMAT = '%(asctime)s [%(levelname)s] %(module)s: %(message)s'
    LOG_MAX_BYTES = 10 * 1024 * 1024  # 10MB
    LOG_BACKUP_COUNT = 5 