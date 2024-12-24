from flask import Blueprint, render_template, request, jsonify
from flask_login import login_required, current_user
from app.models.project import Project
from app.models.sprint import Sprint
from app.models.story import Story
from app.extensions import db
from app import logger
from datetime import datetime

bp = Blueprint('kanban', __name__)

@bp.route('/')
@bp.route('/<int:project_id>')
@login_required
def index(project_id=None):
    """看板首页"""
    try:
        logger.info(f"User {current_user.username} accessing kanban board")
        
        projects = Project.query.all()
        logger.debug(f"Found {len(projects)} total projects")
        
        current_project = None
        active_sprint = None
        todo_stories = []
        doing_stories = []
        done_stories = []

        if project_id:
            current_project = Project.query.get_or_404(project_id)
            logger.info(f"Viewing kanban for project: {current_project.name} (ID: {project_id})")
            
            active_sprint = Sprint.query.filter_by(
                project_id=current_project.id,
                status='active'
            ).first()
            
            if active_sprint:
                logger.info(f"Found active sprint: {active_sprint.name} (ID: {active_sprint.id})")
                
                todo_stories = Story.query.filter_by(
                    sprint_id=active_sprint.id,
                    status=Story.KANBAN_TODO
                ).all()
                
                doing_stories = Story.query.filter_by(
                    sprint_id=active_sprint.id,
                    status=Story.KANBAN_DOING
                ).all()
                
                done_stories = Story.query.filter_by(
                    sprint_id=active_sprint.id,
                    status=Story.KANBAN_DONE
                ).all()
                
                # 记录每个状态的故事数量和详细信息
                logger.debug(f"Stories in TODO: {len(todo_stories)} - IDs: {[s.id for s in todo_stories]}")
                logger.debug(f"Stories in DOING: {len(doing_stories)} - IDs: {[s.id for s in doing_stories]}")
                logger.debug(f"Stories in DONE: {len(done_stories)} - IDs: {[s.id for s in done_stories]}")
                
                # 计算完成率
                total_stories = len(todo_stories) + len(doing_stories) + len(done_stories)
                completion_rate = (len(done_stories) / total_stories * 100) if total_stories > 0 else 0
                logger.info(f"Sprint progress - Total stories: {total_stories}, Completion rate: {completion_rate:.1f}%")
            else:
                logger.info(f"No active sprint found for project {current_project.name}")
        else:
            logger.info("No project selected, showing project selection view")
        
        return render_template('kanban/index.html',
                             projects=projects,
                             current_project=current_project,
                             active_sprint=active_sprint,
                             todo_stories=todo_stories,
                             doing_stories=doing_stories,
                             done_stories=done_stories)
    except Exception as e:
        logger.error(f"Error in kanban index view: {str(e)}", exc_info=True)
        return jsonify({'error': str(e)}), 500

@bp.route('/api/stories/<int:story_id>/status', methods=['PUT'])
@login_required
def update_story_status(story_id):
    """更新故事状态"""
    try:
        story = Story.query.get_or_404(story_id)
        data = request.get_json()
        new_status = data.get('status')
        
        logger.info(f"Story status update requested by {current_user.username} - Story ID: {story_id}, Title: {story.title}")
        
        # 获取Sprint信息用于日志
        sprint = Sprint.query.get(story.sprint_id) if story.sprint_id else None
        if sprint:
            logger.debug(f"Story belongs to sprint: {sprint.name} (ID: {sprint.id})")
        
        if new_status not in [Story.KANBAN_TODO, Story.KANBAN_DOING, Story.KANBAN_DONE]:
            logger.warning(f"Invalid status value requested: {new_status}")
            return jsonify({
                'status': 'error',
                'message': 'Invalid status value'
            }), 400
        
        # 记录状态变更
        old_status = story.status
        story.status = new_status
        
        # 记录状态变更的时间戳
        if new_status == Story.KANBAN_DOING and old_status == Story.KANBAN_TODO:
            story.started_at = datetime.now()
            logger.info(f"Story started at: {story.started_at}")
        elif new_status == Story.KANBAN_DONE and old_status != Story.KANBAN_DONE:
            story.completed_at = datetime.now()
            logger.info(f"Story completed at: {story.completed_at}")
            
            # 如果是完成状态,计算处理时间
            if story.started_at:
                processing_time = story.completed_at - story.started_at
                logger.info(f"Story processing time: {processing_time}")
        
        try:
            db.session.commit()
            
            # 更新后重新计算Sprint的完成率
            if sprint:
                sprint_stories = Story.query.filter_by(sprint_id=sprint.id).all()
                total_stories = len(sprint_stories)
                completed_stories = len([s for s in sprint_stories if s.status == Story.KANBAN_DONE])
                completion_rate = (completed_stories / total_stories * 100) if total_stories > 0 else 0
                logger.info(f"Updated sprint progress - Total: {total_stories}, Completed: {completed_stories}, Rate: {completion_rate:.1f}%")
            
            logger.info(f"Story status updated successfully - Status changed: {old_status} -> {new_status}")
            return jsonify({
                'status': 'success',
                'story': story.to_dict(),
                'sprint_stats': {
                    'total_stories': total_stories,
                    'completed_stories': completed_stories,
                    'completion_rate': completion_rate
                } if sprint else None
            })
        except Exception as e:
            db.session.rollback()
            logger.error(f"Database error while updating story status: {str(e)}", exc_info=True)
            return jsonify({'error': str(e)}), 500
    except Exception as e:
        logger.error(f"Error in update_story_status: {str(e)}", exc_info=True)
        return jsonify({'error': str(e)}), 500