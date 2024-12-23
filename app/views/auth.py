from flask import Blueprint, render_template, redirect, url_for, flash, request
from flask_login import login_user, logout_user, login_required, current_user
from werkzeug.urls import url_parse
from app.models.user import User
from app import db, logger
from app.forms.auth import LoginForm

bp = Blueprint("auth", __name__)


@bp.route("/login", methods=["GET", "POST"])
def login():
    if current_user.is_authenticated:
        return redirect(url_for("backlog.index"))

    if request.method == "GET":
        logger.info("Login page accessed")

    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(username=form.username.data).first()

        if user is None or not user.check_password(form.password.data):
            logger.warning(f"Failed login attempt for user: {form.username.data}")
            flash("ユーザー名またはパスワードが正しくありません", "error")
            return redirect(url_for("auth.login"))

        login_user(user, remember=form.remember_me.data)
        user.update_last_login()
        logger.info(f"User {user.username} logged in successfully")

        next_page = url_for("backlog.index")

        flash("ログインに成功しました", "success")
        return redirect(next_page)

    return render_template("auth/login.html", title="ログイン", form=form)


@bp.route("/logout")
@login_required
def logout():
    logout_user()
    logger.info("User logged out")
    flash("ログアウトしました", "info")
    return redirect(url_for("auth.login"))
