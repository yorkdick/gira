from flask import Blueprint, render_template
from flask_login import login_required

bp = Blueprint("main", __name__)


@bp.route("/")
@bp.route("/index")
@login_required
def index():
    """メインページを表示"""
    return render_template("main/index.html", title="プロジェクト一覧")
