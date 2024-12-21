from flask import Blueprint, render_template
from flask_login import login_required, current_user
from app.models.project import Project
from app import db

bp = Blueprint("backlog", __name__)


@bp.route("/backlog")
@bp.route("/backlog/<int:project_id>")
@login_required
def index(project_id=None):
    """バックログページを表示"""
    projects = Project.query.all()
    current_project = None

    if project_id:
        current_project = Project.query.get_or_404(project_id)
    elif projects:
        current_project = projects[0]

    return render_template(
        "backlog/index.html",
        title="バックログ",
        projects=projects,
        current_project=current_project,
    )
