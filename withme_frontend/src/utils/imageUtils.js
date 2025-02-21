import { API_URL } from "../constant";

export const getImageUrl = (imageUrl) => {
  //console.log("Original imageUrl:", imageUrl); // 입력받은 원본 URL

  if (!imageUrl) {
    //console.log("No image URL provided, using default image");
    return "/assets/images/default-pet-image.png";
  }

  if (imageUrl.startsWith("http")) {
    console.log("Full URL detected:", imageUrl);
    return imageUrl;
  }

  // imageUrl이 /api/pets/image/로 시작하는 경우, 파일명만 추출
  const filename = imageUrl.includes("/api/pets/image/")
    ? imageUrl.split("/api/pets/image/").pop()
    : imageUrl;

  const fullUrl = `${API_URL}pets/image/${filename}`;
  //console.log("Constructed URL:", fullUrl);
  return fullUrl;
};
