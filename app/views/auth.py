from flask import Blueprint, render_template, redirect, url_for, flash, request
from flask_login import login_user, logout_user, login_required, current_user
from werkzeug.urls import url_parse
from app.models.user import User
from app import db, logger
from app.forms.auth import LoginForm, RegistrationForm, ResetPasswordRequestForm, ResetPasswordForm
from datetime import datetime

bp = Blueprint("auth", __name__)


@bp.route("/login", methods=["GET", "POST"])
def login():
    """用户登录"""
    try:
        # 如果用户已经登录,直接跳转到首页
        if current_user.is_authenticated:
            logger.info(f"Already authenticated user {current_user.username} accessing login page")
            return redirect(url_for("backlog.index"))

        # 记录访问信息
        if request.method == "GET":
            logger.info(f"Login page accessed from IP: {request.remote_addr}")

        form = LoginForm()
        if form.validate_on_submit():
            username = form.username.data
            logger.info(f"Login attempt for user: {username} from IP: {request.remote_addr}")
            
            user = User.query.filter_by(username=username).first()

            if user is None:
                logger.warning(f"Login failed - User not found: {username}")
                flash("ユーザー名またはパスワードが正しくありません", "error")
                return redirect(url_for("auth.login"))
                
            if not user.check_password(form.password.data):
                logger.warning(f"Login failed - Invalid password for user: {username}")
                flash("ユーザー名またはパスワードが正しくありません", "error")
                return redirect(url_for("auth.login"))
                
            if not user.is_active:
                logger.warning(f"Login failed - Inactive user: {username}")
                flash("アカウントが無効です", "error")
                return redirect(url_for("auth.login"))

            # 登录成功
            login_user(user, remember=form.remember_me.data)
            
            # 更新最后登录时间
            old_last_login = user.last_login
            user.update_last_login()
            
            logger.info(f"User {username} logged in successfully - Previous login: {old_last_login}")
            
            # 记录登录会话信息
            logger.debug(f"Session info - Remember me: {form.remember_me.data}, User ID: {user.id}")

            next_page = url_for("backlog.index")
            flash("ログインに成功しました", "success")
            return redirect(next_page)

        return render_template("auth/login.html", title="ログイン", form=form)
    except Exception as e:
        logger.error(f"Error in login view: {str(e)}", exc_info=True)
        flash("エラーが発生しました", "error")
        return redirect(url_for("auth.login"))


@bp.route("/logout")
@login_required
def logout():
    """用户登出"""
    try:
        username = current_user.username
        user_id = current_user.id
        last_login = current_user.last_login
        
        logout_user()
        
        logger.info(f"User logged out - Username: {username}, ID: {user_id}")
        logger.debug(f"Logout details - Last login: {last_login}, Session duration: {datetime.now() - last_login}")
        
        flash("ログアウトしました", "info")
        return redirect(url_for("auth.login"))
    except Exception as e:
        logger.error(f"Error in logout view: {str(e)}", exc_info=True)
        flash("エラーが発生しました", "error")
        return redirect(url_for("auth.login"))


@bp.route("/register", methods=["GET", "POST"])
def register():
    """用户注册"""
    try:
        if current_user.is_authenticated:
            logger.info(f"Already authenticated user {current_user.username} accessing register page")
            return redirect(url_for("backlog.index"))

        if request.method == "GET":
            logger.info(f"Register page accessed from IP: {request.remote_addr}")

        form = RegistrationForm()
        if form.validate_on_submit():
            username = form.username.data
            email = form.email.data
            logger.info(f"Registration attempt - Username: {username}, Email: {email}, IP: {request.remote_addr}")
            
            # 检查用户名是否已存在
            if User.query.filter_by(username=username).first():
                logger.warning(f"Registration failed - Username already exists: {username}")
                flash("このユーザー名は既に使用されています", "error")
                return redirect(url_for("auth.register"))
                
            # 检查邮箱是否已存在
            if User.query.filter_by(email=email).first():
                logger.warning(f"Registration failed - Email already exists: {email}")
                flash("このメールアドレスは既に使用されています", "error")
                return redirect(url_for("auth.register"))

            # 创建新用户
            user = User(
                username=username,
                email=email,
                is_active=True
            )
            user.set_password(form.password.data)
            
            try:
                db.session.add(user)
                db.session.commit()
                logger.info(f"User registered successfully - Username: {username}, ID: {user.id}")
                
                # 自动登录
                login_user(user)
                user.update_last_login()
                logger.info(f"New user {username} logged in automatically after registration")
                
                flash("登録が完了しました", "success")
                return redirect(url_for("backlog.index"))
            except Exception as e:
                db.session.rollback()
                logger.error(f"Database error while registering user: {str(e)}", exc_info=True)
                flash("登録に失敗しました", "error")
                return redirect(url_for("auth.register"))

        return render_template("auth/register.html", title="新規登録", form=form)
    except Exception as e:
        logger.error(f"Error in register view: {str(e)}", exc_info=True)
        flash("エラーが発生しました", "error")
        return redirect(url_for("auth.register"))


@bp.route("/reset-password-request", methods=["GET", "POST"])
def reset_password_request():
    """密码重置请求"""
    try:
        if current_user.is_authenticated:
            logger.info(f"Already authenticated user {current_user.username} accessing password reset page")
            return redirect(url_for("backlog.index"))

        if request.method == "GET":
            logger.info(f"Password reset page accessed from IP: {request.remote_addr}")

        form = ResetPasswordRequestForm()
        if form.validate_on_submit():
            email = form.email.data
            user = User.query.filter_by(email=email).first()
            
            if user:
                # 生成重置令牌
                token = user.get_reset_password_token()
                logger.info(f"Password reset requested for user: {user.username}, Email: {email}")
                
                # 发送重置邮件
                send_password_reset_email(user, token)
                logger.info(f"Password reset email sent to: {email}")
                
                flash("パスワードリセット手順を記載したメールを送信しました", "info")
                return redirect(url_for("auth.login"))
            else:
                logger.warning(f"Password reset failed - Email not found: {email}")
                flash("このメールアドレスは登録されていません", "error")
                return redirect(url_for("auth.reset_password_request"))

        return render_template("auth/reset_password_request.html", title="パスワードリセット", form=form)
    except Exception as e:
        logger.error(f"Error in reset_password_request view: {str(e)}", exc_info=True)
        flash("エラーが発生しました", "error")
        return redirect(url_for("auth.reset_password_request"))


@bp.route("/reset-password/<token>", methods=["GET", "POST"])
def reset_password(token):
    """密码重置"""
    try:
        if current_user.is_authenticated:
            logger.info(f"Already authenticated user {current_user.username} accessing password reset confirmation page")
            return redirect(url_for("backlog.index"))

        # 验证令牌
        user = User.verify_reset_password_token(token)
        if not user:
            logger.warning(f"Invalid password reset token: {token}")
            flash("無効なトークンです", "error")
            return redirect(url_for("auth.login"))

        logger.info(f"Valid password reset token for user: {user.username}")
        
        form = ResetPasswordForm()
        if form.validate_on_submit():
            # 更新密码
            user.set_password(form.password.data)
            db.session.commit()
            
            logger.info(f"Password reset successful for user: {user.username}")
            flash("パスワードがリセットされました", "success")
            return redirect(url_for("auth.login"))

        return render_template("auth/reset_password.html", title="パスワードリセット", form=form)
    except Exception as e:
        logger.error(f"Error in reset_password view: {str(e)}", exc_info=True)
        flash("エラーが発生しました", "error")
        return redirect(url_for("auth.login"))
