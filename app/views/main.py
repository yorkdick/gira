from flask import Blueprint, redirect, url_for
from flask_login import login_required

bp = Blueprint('main', __name__)

@bp.route('/')
@login_required
def index():
    return redirect(url_for('backlog.index'))


@bp.route("/active-sprint")
@bp.route("/active-sprint/<int:project_id>")
@login_required
def active_sprint(project_id=None):
    """アクティブスプリントページを表示（開発中）"""
    projects = Project.query.all()
    current_project = None
    
    if project_id:
        current_project = Project.query.get_or_404(project_id)
    elif projects:
        current_project = projects[0]
        
    return render_template(
        "main/active_sprint.html",
        title="アクティブスプリント",
        projects=projects,
        current_project=current_project,
        active_page='active_sprint'
    )
