import os
import logging
from logging.handlers import RotatingFileHandler
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_login import LoginManager
from flask_migrate import Migrate
from config import Config

# 初期化
db = SQLAlchemy()
login_manager = LoginManager()
login_manager.login_view = "auth.login"
login_manager.login_message = "このページにアクセスするにはログインが必要です。"
migrate = Migrate()

# ロガーの設定
logger = logging.getLogger("gira")
logger.setLevel(logging.INFO)


def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    # 拡張機能の初期化
    db.init_app(app)
    login_manager.init_app(app)
    migrate.init_app(app, db)

    # ログハンドラーの設定
    if app.config["LOG_TYPE"] == "file":
        if not os.path.exists("logs"):
            os.mkdir("logs")
        file_handler = RotatingFileHandler(
            app.config["LOG_FILE"],
            maxBytes=app.config["LOG_MAX_BYTES"],
            backupCount=app.config["LOG_BACKUP_COUNT"],
        )
        file_handler.setFormatter(logging.Formatter(app.config["LOG_FORMAT"]))
        file_handler.setLevel(app.config["LOG_LEVEL"])
        logger.addHandler(file_handler)
    else:
        stream_handler = logging.StreamHandler()
        stream_handler.setFormatter(logging.Formatter(app.config["LOG_FORMAT"]))
        stream_handler.setLevel(app.config["LOG_LEVEL"])
        logger.addHandler(stream_handler)

    logger.info("GIRA application startup")

    # Blueprintの登録
    from app.views import auth, main, backlog

    app.register_blueprint(auth.bp)
    app.register_blueprint(main.bp)
    app.register_blueprint(backlog.bp)

    return app


from app.models import user, project  # モデルの読み込み
