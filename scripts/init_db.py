#!/usr/bin/env python
import os
import sys
from datetime import datetime, timedelta, UTC

# 将项目根目录添加到 Python 路径
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app import create_app, db
from app.models.user import User
from app.models.project import Project
from app.models.story import Story
from app.models.sprint import Sprint
import random

def init_db():
    app = create_app()
    with app.app_context():
        # 清空现有数据
        db.drop_all()
        db.create_all()
        
        # 创建管理员用户
        admin = User(
            username="admin",
            email="admin@example.com",
            first_name="管理者",
            last_name="システム",
            avatar_color="#0052CC"
        )
        admin.set_password("admin123")
        db.session.add(admin)
        
        # 创建测试用户
        test_user = User(
            username="test",
            email="test@example.com",
            first_name="テスト",
            last_name="ユーザー",
            avatar_color="#00875A"
        )
        test_user.set_password("test123")
        db.session.add(test_user)
        
        # 创建示例项目
        gira_project = Project(
            name="GIRA開発プロジェクト",
            key="GIRA",
            description="GIRAプロジェクトの開発用プロジェクト",
            status="active",
            owner=admin
        )
        db.session.add(gira_project)
        
        test_project = Project(
            name="テストプロジェクト",
            key="TEST",
            description="テスト用のプロジェクト",
            status="active",
            owner=test_user
        )
        db.session.add(test_project)
        
        # 为GIRA项目创建Sprint和Story
        # Sprint 1（已完成）
        sprint1 = Sprint(
            name="Sprint 1",
            goal="基本的なユーザー認証システムの実装",
            project=gira_project,
            status="completed",
            start_date=datetime.now(UTC) - timedelta(days=28),
            end_date=datetime.now(UTC) - timedelta(days=14)
        )
        db.session.add(sprint1)

        # Sprint 1的故事
        stories_sprint1 = [
            Story(
                title="ユーザー登録機能の実装",
                description="ユーザー名、メールアドレス、パスワードによる新規ユーザー登録機能を実装する",
                status="done",
                story_points=5,
                priority=Story.PRIORITY_HIGH,
                project=gira_project,
                sprint=sprint1,
                assignee=admin
            ),
            Story(
                title="ログイン機能の実装",
                description="メールアドレスとパスワードによるログイン機能を実装する",
                status="done",
                story_points=3,
                priority=Story.PRIORITY_HIGH,
                project=gira_project,
                sprint=sprint1,
                assignee=admin
            ),
            Story(
                title="パスワードリセット機能の実装",
                description="メールアドレスによるパスワードリセット機能を実装する",
                status="done",
                story_points=3,
                priority=Story.PRIORITY_MEDIUM,
                project=gira_project,
                sprint=sprint1,
                assignee=test_user
            )
        ]
        for story in stories_sprint1:
            db.session.add(story)

        # Sprint 2（进行中）
        sprint2 = Sprint(
            name="Sprint 2",
            goal="プロジェクト管理機能の実装",
            project=gira_project,
            status="active",
            start_date=datetime.now(UTC) - timedelta(days=7),
            end_date=datetime.now(UTC) + timedelta(days=7)
        )
        db.session.add(sprint2)

        # Sprint 2的故事
        stories_sprint2 = [
            Story(
                title="プロジェクト作成機能の実装",
                description="プロジェクト名、キー、説明を入力して新規プロジェクトを作成する機能を実装する",
                status="done",
                story_points=5,
                priority=Story.PRIORITY_HIGH,
                project=gira_project,
                sprint=sprint2,
                assignee=admin
            ),
            Story(
                title="プロジェクト一覧表示機能の実装",
                description="ユーザーが参加しているプロジェクトの一覧を表示する機能を実装する",
                status="doing",
                story_points=3,
                priority=Story.PRIORITY_HIGH,
                project=gira_project,
                sprint=sprint2,
                assignee=admin
            ),
            Story(
                title="プロジェクト詳細表示機能の実装",
                description="プロジェクトの詳細情報（メンバー、進捗状況など）を表示する機能を実装する",
                status="todo",
                story_points=5,
                priority=Story.PRIORITY_MEDIUM,
                project=gira_project,
                sprint=sprint2,
                assignee=test_user
            )
        ]
        for story in stories_sprint2:
            db.session.add(story)

        # Backlog中的故事
        backlog_stories = [
            Story(
                title="スプリントボード機能の実装",
                description="カンバンボード形式でスプリント内のストーリーを管理する機能を実装する",
                status="todo",
                story_points=8,
                priority=Story.PRIORITY_HIGH,
                project=gira_project,
                assignee=admin
            ),
            Story(
                title="バーンダウンチャートの実装",
                description="スプリントの進捗を可視化するバーンダウンチャートを実装する",
                status="todo",
                story_points=5,
                priority=Story.PRIORITY_MEDIUM,
                project=gira_project,
                assignee=test_user
            ),
            Story(
                title="チーム管理機能の実装",
                description="プロジェクトメンバーの追加、削除、権限管理機能を実装する",
                status="todo",
                story_points=5,
                priority=Story.PRIORITY_MEDIUM,
                project=gira_project
            ),
            Story(
                title="アクティビティログの実装",
                description="プロジェクト内の活動履歴を記録・表示する機能を実装する",
                status="todo",
                story_points=3,
                priority=Story.PRIORITY_LOW,
                project=gira_project
            )
        ]
        for story in backlog_stories:
            db.session.add(story)

        db.session.commit()
        print("データベースの初期化が完了しました。")
        print("\n初期ユーザー:")
        print("1. 管理者")
        print("   ユーザー名: admin")
        print("   パスワード: admin123")
        print("\n2. テストユーザー")
        print("   ユーザー名: test")
        print("   パスワード: test123")

if __name__ == "__main__":
    init_db() 