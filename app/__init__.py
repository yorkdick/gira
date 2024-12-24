import os
import logging
from logging.handlers import RotatingFileHandler
from flask import Flask, redirect, url_for
from config import Config
from app.extensions import db, migrate, login_manager
from flask_login import login_required

# ロガーの設定
logger = logging.getLogger("gira")
logger.setLevel(logging.INFO)

def create_app(test_config=None):
    app = Flask(__name__)
    
    if test_config is None:
        app.config.from_object(Config)
    else:
        # 如果传入了测试配置，使用测试配置
        app.config.from_object(test_config)

    # 拡張機能の初期化
    db.init_app(app)
    migrate.init_app(app, db)
    login_manager.init_app(app)

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
    from app.views import auth, main, backlog, kanban, project

    # 先注册认证相关路由
    app.register_blueprint(auth.bp)

    # 然后注册其他路由
    app.register_blueprint(main.bp)
    app.register_blueprint(backlog.bp)
    app.register_blueprint(kanban.bp, url_prefix='/kanban')
    app.register_blueprint(project.bp, url_prefix='')

    # 添加根路由重定向到 backlog
    @app.route('/')
    @login_required
    def index():
        return redirect(url_for('backlog.index'))

    from . import commands
    app.cli.add_command(commands.init_db_command)
    app.cli.add_command(commands.create_test_data)

    return app

# モデルの読み込み
from app.models import user, project, story, sprint
