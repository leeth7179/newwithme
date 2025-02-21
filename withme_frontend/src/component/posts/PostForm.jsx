import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { API_URL } from "../../constant";
import { fetchWithAuth } from "../../common/fetchWithAuth";
import { Editor } from "@tinymce/tinymce-react";
import "../../assets/css/posts/posts.css";

const PostForm = () => {
  const [post, setPost] = useState({
    title: "",
    content: "<p></p>",
    postCategory: ""
  });

  const navigate = useNavigate();
  const { id } = useParams();

  const fetchPost = async () => {
    try {
      const response = await fetchWithAuth(`${API_URL}posts/${id}`);
      if (!response.ok) {
        throw new Error(`HTTP 오류! 상태 코드: ${response.status}`);
      }
      const data = await response.json();

      setPost({
        title: data.title || "",
        content: data.content || "<p></p>",
        postCategory: data.postCategory || ""
      });
    } catch (error) {
      console.error("게시글 가져오기 실패:", error.message);
      alert("게시글을 불러오는데 실패했습니다.");
      navigate("/posts");
    }
  };

  useEffect(() => {
    if (id) {
      fetchPost();
    }
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPost((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditorChange = (content) => {
    const safeContent = content || "<p></p>";
    //console.log("에디터 내용 변경:", safeContent);

    setPost((prev) => ({
      ...prev,
      content: safeContent
    }));
  };

  const handleImageUpload = async (blobInfo, progress) => {
    try {
      const formData = new FormData();
      formData.append("image", blobInfo.blob(), blobInfo.filename());

      const response = await fetch(`${API_URL}posts/upload`, {
        method: "POST",
        credentials: "include",
        body: formData
      });

      if (!response.ok) {
        throw new Error("이미지 업로드 실패");
      }

      const result = await response.json();
      const baseUrl = API_URL.replace(/\/$/, "");
      const imagePath = result.imageUrl
        .replace(/^\/api/, "")
        .replace(/^\//, "");
      const imageUrl = `${baseUrl}/${imagePath}`;

      console.log("업로드된 이미지 URL:", imageUrl);
      return imageUrl;
    } catch (error) {
      console.error("이미지 업로드 오류:", error);
      throw error;
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const cleanContent = post.content?.trim() || "";
    const contentWithoutTags = cleanContent.replace(/<[^>]*>/g, "").trim();

    if (!post.title.trim()) {
      alert("제목을 입력해주세요.");
      return;
    }

    if (!contentWithoutTags) {
      alert("내용을 입력해주세요.");
      return;
    }

    if (!post.postCategory) {
      alert("카테고리를 선택해주세요.");
      return;
    }

    try {
      const method = id ? "PUT" : "POST";
      const endpoint = `${API_URL}posts${id ? `/${id}` : ""}`;

      const parser = new DOMParser();
      const doc = parser.parseFromString(post.content, "text/html");
      const firstImage = doc.querySelector("img");
      const thumbnailUrl = firstImage
        ? firstImage.src.replace(
            /^.*\/api\/posts\/image\//,
            "/api/posts/image/"
          )
        : null;

      const postData = {
        title: post.title.trim(),
        content: post.content,
        postCategory: post.postCategory,
        thumbnailUrl: thumbnailUrl
      };

      console.log("서버로 전송할 데이터:", JSON.stringify(postData, null, 2));

      const response = await fetchWithAuth(endpoint, {
        method,
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(postData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.error || `HTTP 오류! 상태 코드: ${response.status}`
        );
      }

      alert(id ? "게시글이 수정되었습니다." : "게시글이 등록되었습니다.");
      navigate("/posts");
    } catch (error) {
      console.error("게시글 저장 실패:", error);
      alert("게시글 저장에 실패했습니다. " + error.message);
    }
  };

  return (
    <div className="post_form_container">
      <h1 className="post_form_title">
        {id ? "커뮤니티 수정" : "커뮤니티 등록"}
      </h1>

      <form onSubmit={handleSubmit} className="post_form">
        <div className="form_group">
          <label className="form_label">제목:</label>
          <input
            type="text"
            name="title"
            value={post.title}
            onChange={handleChange}
            required
            className="form_input"
            placeholder="제목을 입력해주세요"
          />
        </div>

        <div className="form_group">
          <label className="form_label">내용:</label>
          <div className="editor_wrapper">
            <Editor
              apiKey="ex9u265c1zhjyhpymup3s4hl475tatjva16c9xn4yi6kk0rg"
              value={post.content}
              onEditorChange={handleEditorChange}
              init={{
                height: 500,
                menubar: false,
                plugins: [
                  "advlist",
                  "autolink",
                  "lists",
                  "link",
                  "image",
                  "charmap",
                  "preview",
                  "searchreplace",
                  "visualblocks",
                  "code",
                  "fullscreen",
                  "insertdatetime",
                  "media",
                  "table",
                  "code",
                  "help",
                  "wordcount"
                ],
                toolbar:
                  "undo redo | blocks | bold italic forecolor | alignleft aligncenter " +
                  "alignright alignjustify | bullist numlist outdent indent | " +
                  "removeformat | image | help",
                images_upload_handler: handleImageUpload,
                automatic_uploads: true,
                images_reuse_filename: true,
                paste_data_images: true,
                content_style:
                  "body { font-family:Helvetica,Arial,sans-serif; font-size:14px }"
              }}
            />
          </div>
        </div>

        <div className="form_group">
          <label className="form_label">카테고리:</label>
          <select
            name="postCategory"
            value={post.postCategory}
            onChange={handleChange}
            required
            className="form_select">
            <option value="">카테고리 선택</option>
            <option value="펫푸드">펫푸드</option>
            <option value="질문/꿀팁">질문/꿀팁</option>
            <option value="펫일상">펫일상</option>
            <option value="펫수다">펫수다</option>
            <option value="행사/정보">행사/정보</option>
          </select>
        </div>

        <div className="form_button_group">
          <button type="submit" className="form_button submit_button">
            저장
          </button>
          <button
            type="button"
            onClick={() => navigate("/posts")}
            className="form_button cancel_button">
            취소
          </button>
        </div>
      </form>
    </div>
  );
};

export default PostForm;
