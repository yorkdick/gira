from app.models.user import User
from app.models.project import Project
from app.models.story import Story
from app.models.sprint import Sprint
from app.views import kanban


def create_app(config_name):
    # 现有代码...

    # 注册Blueprint
    app.register_blueprint(kanban.bp, url_prefix="/kanban")

    # 现有代码...
