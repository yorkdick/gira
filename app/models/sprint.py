from datetime import datetime
from app import db


class Sprint(db.Model):
    __tablename__ = "sprints"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    start_date = db.Column(db.DateTime)
    end_date = db.Column(db.DateTime)
    status = db.Column(db.String(20), default="planning")  # planning, active, completed
    project_id = db.Column(db.Integer, db.ForeignKey("projects.id"), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(
        db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )
    goal = db.Column(db.String(255))

    # 关联关系
    project = db.relationship("Project", backref=db.backref("sprints", lazy=True))

    def to_dict(self):
        """将冲刺对象转换为字典格式"""
        data = {
            "id": self.id,
            "name": self.name,
            "goal": self.goal,
            "status": self.status,
            "project_id": self.project_id,
            "start_date": self.start_date.isoformat() if self.start_date else None,
            "end_date": self.end_date.isoformat() if self.end_date else None,
            "created_at": self.created_at.isoformat() if self.created_at else None,
            "updated_at": self.updated_at.isoformat() if self.updated_at else None,
        }

        # 添加关联对象的信息
        if self.project:
            data["project"] = {"id": self.project.id, "name": self.project.name}

        # 添加故事统计信息
        stories = [story for story in self.stories]
        total_stories = len(stories)
        completed_stories = len([s for s in stories if s.status == "done"])
        data["stats"] = {
            "total_stories": total_stories,
            "completed_stories": completed_stories,
            "completion_rate": (completed_stories / total_stories * 100)
            if total_stories > 0
            else 0,
        }

        return data

    def __repr__(self):
        return f"<Sprint {self.name}>"
