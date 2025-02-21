import React from "react";
import PropTypes from "prop-types";
import "../../assets/css/posts/posts.css";

const CommentComponent = ({
  comment,
  depth = 1,
  user,
  isLoggedIn,
  setIsHovered,
  isHovered,
  isEditing,
  setIsEditing,
  startEditing,
  handleEditSubmit,
  handleDelete,
  toggleReplyForm,
  showReplyForm,
  handleReplySubmit,
  replyContent,
  setReplyContent,
  editContent,
  setEditContent,
  postId
}) => {
  return (
    <div className="comment_warp">
      <div
        onMouseEnter={() => setIsHovered(comment.id)}
        onMouseLeave={() => setIsHovered(null)}>
        {isEditing === comment.id ? (
          <form
            className="edit_form"
            onSubmit={(e) => handleEditSubmit(e, comment.id)}>
            <textarea
              value={editContent}
              onChange={(e) => setEditContent(e.target.value)}
              rows="3"
              required
            />
            <div className="button_group">
              <button type="button" onClick={() => setIsEditing(null)}>
                취소
              </button>
              <button className="submit" type="submit">
                저장
              </button>
            </div>
          </form>
        ) : (
          <div className={`comment_depth depth-${depth}`}>
            <div className="comment_area_warp">
              <div className="comment_area">
                <div className="area_top">
                  <span className="user_name">{comment.userName}</span>
                  {comment.updateTime &&
                  comment.updateTime !== comment.regTime ? (
                    <div className="date">
                      수정일:{" "}
                      {new Date(comment.updateTime).toLocaleString("ko-KR", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit",
                        hour: "2-digit",
                        minute: "2-digit"
                      })}
                    </div>
                  ) : (
                    <div className="date">
                      작성일:{" "}
                      {comment.regTime &&
                        new Date(comment.regTime).toLocaleString("ko-KR", {
                          year: "numeric",
                          month: "2-digit",
                          day: "2-digit",
                          hour: "2-digit",
                          minute: "2-digit"
                        })}
                    </div>
                  )}
                </div>
                <div className="content">
                  <p>{comment.content}</p>
                </div>
                <div className="content_btn_warp">
                  {isHovered === comment.id && (
                    <div className="comment_actions">
                      {user?.id === comment.userId && (
                        <>
                          <span onClick={() => handleDelete(comment.id)}>
                            삭제
                          </span>
                          <span
                            onClick={() =>
                              startEditing(comment.id, comment.content)
                            }>
                            수정
                          </span>
                        </>
                      )}
                    </div>
                  )}
                  <span
                    className="reply_form_open"
                    onClick={() => toggleReplyForm(comment.id)}>
                    답글
                  </span>
                </div>
              </div>

              {showReplyForm === comment.id && (
                <form
                  className="reply_form"
                  onSubmit={(e) => handleReplySubmit(e, comment.id)}>
                  <textarea
                    value={replyContent}
                    onChange={(e) => setReplyContent(e.target.value)}
                    placeholder="답글을 작성하세요"
                    rows="3"
                    required
                  />
                  <div className="button_group">
                    <button type="button" onClick={() => toggleReplyForm(null)}>
                      취소
                    </button>
                    <button className="submit" type="submit">
                      저장
                    </button>
                  </div>
                </form>
              )}
            </div>
          </div>
        )}

        {/* 대댓글 영역 */}
        {comment.replies && comment.replies.length > 0 && (
          <ul className="replies">
            {comment.replies.map((reply) => (
              <li
                className="reply_item"
                key={reply.id}
                style={{
                  "--depth": depth + 1
                }}>
                <CommentComponent
                  comment={reply}
                  depth={depth + 1}
                  user={user}
                  isLoggedIn={isLoggedIn}
                  setIsHovered={setIsHovered}
                  isHovered={isHovered}
                  isEditing={isEditing}
                  setIsEditing={setIsEditing}
                  startEditing={startEditing}
                  handleEditSubmit={handleEditSubmit}
                  handleDelete={handleDelete}
                  toggleReplyForm={toggleReplyForm}
                  showReplyForm={showReplyForm}
                  handleReplySubmit={handleReplySubmit}
                  replyContent={replyContent}
                  setReplyContent={setReplyContent}
                  editContent={editContent}
                  setEditContent={setEditContent}
                  postId={postId}
                />
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

CommentComponent.propTypes = {
  comment: PropTypes.shape({
    id: PropTypes.number.isRequired,
    userName: PropTypes.string.isRequired,
    content: PropTypes.string.isRequired,
    updateTime: PropTypes.string,
    regTime: PropTypes.string.isRequired,
    userId: PropTypes.number.isRequired,
    replies: PropTypes.array
  }).isRequired,
  depth: PropTypes.number,
  user: PropTypes.shape({
    id: PropTypes.number,
    name: PropTypes.string
  }),
  isLoggedIn: PropTypes.bool.isRequired,
  setIsHovered: PropTypes.func.isRequired,
  isHovered: PropTypes.number,
  isEditing: PropTypes.number,
  setIsEditing: PropTypes.func.isRequired,
  startEditing: PropTypes.func.isRequired,
  handleEditSubmit: PropTypes.func.isRequired,
  handleDelete: PropTypes.func.isRequired,
  toggleReplyForm: PropTypes.func.isRequired,
  showReplyForm: PropTypes.number,
  handleReplySubmit: PropTypes.func.isRequired,
  replyContent: PropTypes.string,
  setReplyContent: PropTypes.func.isRequired,
  editContent: PropTypes.string,
  setEditContent: PropTypes.func.isRequired,
  postId: PropTypes.string.isRequired
};

export default CommentComponent;
