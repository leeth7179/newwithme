import { Button, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { API_URL } from "../../constant";
import { useNavigate } from "react-router-dom";
import { fetchWithoutAuth } from "../../common/fetchWithAuth";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronRight } from "@fortawesome/free-solid-svg-icons";

export default function SignupDoctor() {
    // 입력된 회원 정보를 저장할 상태 변수
    const [member, setMember] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        phone: "",
        address: "",
    });

    const [nameError, setNameError] = useState("");
    const [emailError, setEmailError] = useState("");
    const [emailMessage, setEmailMessage] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [confirmPasswordError, setConfirmPasswordError] = useState("");
    const [phoneError, setPhoneError] = useState("");
    const [isEmailVerified, setIsEmailVerified] = useState(false);

    const navigate = useNavigate();

    // 이메일 중복 체크 함수
    const checkEmailDuplicate = async () => {
        if (!member.email.includes("@")) return;

        try {
            const response = await axios.get(`${API_URL}members/checkEmail`, { params: { email: member.email } });
            const result = await response.data;

            if (result.status === "available") {
                setEmailMessage("사용 가능한 이메일입니다.");
                setEmailError("");
                setIsEmailVerified(true);
            } else if (result.status === "duplicate") {
                setEmailMessage("이미 사용중인 이메일입니다.");
                setEmailError(result.message);
                setIsEmailVerified(false);
            }
        } catch (error) {
            console.error("이메일 중복 체크 실패:", error.message);
        }
    };

    // 이름 유효성 검사
    const validateName = (name) => {
        const nameRegex = /^[가-힣]{2,8}$/;
        if (!nameRegex.test(name)) {
            return "이름은 한글로 2~8자만 입력 가능합니다.";
        }
        return "";
    };

    // 비밀번호 유효성 검사
    const validatePassword = (password) => {
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$/;
        if (!passwordRegex.test(password)) {
            return "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8~16자여야 합니다.";
        }
        return "";
    };

    // 비밀번호 확인 함수
    const checkPasswordMatch = (value) => {
        if (member.password !== value) {
            setConfirmPasswordError("비밀번호가 일치하지 않습니다.");
        } else {
            setConfirmPasswordError("");
        }
    };

    // 전화번호 유효성 검사
    const validatePhoneNumber = (phone) => {
        const phoneRegex = /^[0-9]{11}$/;
        if (!phoneRegex.test(phone)) {
            return "전화번호는 올바른 형식의 숫자만 입력 가능합니다.";
        }
        return "";
    };

    // 회원 정보 입력 시 상태 변경
    const onMemberChange = (event) => {
        const { name, value } = event.target;

        // 이름 유효성 검사
        if (name === "name") {
            setMember({ ...member, [name]: value });
            setNameError(validateName(value));
        } else if (name === "phone") {
            setMember({ ...member, [name]: value });
            setPhoneError(validatePhoneNumber(value));
        } else {
            setMember({ ...member, [name]: value });
        }

        if (name === "password") {
            // 비밀번호 변경 시 유효성 체크
            setPasswordError(validatePassword(value));
        }

        if (name === "password" || name === "confirmPassword") {
            checkPasswordMatch(value);
        }
    };

    const formatPhoneNumber = (phone) => {
        if (phone.length === 11) {
            return `${phone.slice(0, 3)}-${phone.slice(3, 7)}-${phone.slice(7)}`;
        }
        return phone;
    };

    // 회원가입 처리
    const handleOnSubmit = async () => {
        try {
            if (!isEmailVerified) {
                alert("이메일 중복 확인을 먼저 해주세요.");
                return;
            }

            console.log("회원가입 시작");

            // 변환된 전화번호로 회원 정보 업데이트
            const requestMember = {
                ...member,
                phone: formatPhoneNumber(member.phone),
                role: "PENDING_DOCTOR",
            };
            const requestOptions = {
                method: "POST",
                body: JSON.stringify(requestMember),
            };

            const response = await fetchWithoutAuth(`${API_URL}members/register`, requestOptions);

            if (response.ok) {
                alert("회원가입이 완료되었습니다. 관리자의 권한 승인 후 관련 서비스 이용이 가능합니다.");
                navigate("/doctorSignupSuccess", { state: { name: member.name } });
            } else {
                const errorData = await response.json();
                alert(`회원가입 실패: ${errorData.message || "오류 발생"}`);
            }
        } catch (error) {
            console.error("회원가입 중 오류 발생:", error.message);
            alert("회원가입 실패: 네트워크 또는 서버 오류");
        }
    };

    // 일반 가입 페이지로 이동
    const navigateToRegister = () => {
        const isConfirmed = window.confirm(
            "일반 회원으로 가입하시겠습니까? 확인시 지금까지 입력한 내용은 모두 초기화됩니다."
        );

        if (isConfirmed) {
            navigate("/registerMember");
        }
    };

    // 회원가입 버튼 비활성화 조건
    const isFormValid =
        !nameError &&
        !emailError &&
        !passwordError &&
        !confirmPasswordError &&
        !phoneError &&
        isEmailVerified &&
        member.name.trim() !== "" &&
        member.email.trim() !== "" &&
        member.password.trim() !== "" &&
        member.confirmPassword.trim() !== "" &&
        member.address.trim() !== "" &&
        member.phone.trim() !== "";

    return (
        <div className="container">
          <h1 style={{ marginTop: "60px" }}>회원가입</h1>
            <div className="description-container">
                <div className="description box">
                    <img src="/assets/images/icon/file-check.png" alt="file-check" className="icon" />
                    <p>약관동의</p>
                </div>
                <FontAwesomeIcon icon={faChevronRight} className="fontawesome-icon" />
                <div className="description box">
                    <img src="/assets/images/icon/user-pen-color.png" alt="user-pen" className="icon" />
                    <p style={{ color: "#ff7c24" }}>회원정보 입력</p>
                </div>
                <FontAwesomeIcon icon={faChevronRight} className="fontawesome-icon" />
                <div className="description">
                    <img src="/assets/images/icon/thumbs-up.png" alt="thumbs-up" className="icon" />
                    <p>가입완료</p>
                </div>
            </div>

            <div style={{ display: "flex", flexDirection: "row", justifyContent: "space-between", marginTop: "20px", marginBottom: "30px" }}>
                <Button
                    onClick={navigateToRegister}
                    style={{
                        width: "200px",
                        height: "50px",
                        backgroundColor: "#F8F8F8",
                        color: "#ff7c24",
                        fontSize: "1.1em",
                        marginRight: "20px",
                        borderRadius: "8px"
                    }}
                >
                    일반 회원
                </Button>
                <Button
                    style={{
                        width: "200px",
                        height: "50px",
                        backgroundColor: "#ff7c24",
                        color: "white",
                        fontSize: "1.1em",
                        marginRight: "20px",
                        borderRadius: "8px"
                    }}
                >
                    수의사 회원
                </Button>
            </div>

            <div style={{ display: "flex", flexDirection: "column", alignItems: "center", marginTop: "20px" }}>
                <TextField
                    label="이름"
                    name="name"
                    value={member.name}
                    onChange={onMemberChange}
                    style={{ width: "400px", marginBottom: "10px" }}
                    placeholder="한글 2 ~ 8자 입력 가능"
                    error={!!nameError}
                    helperText={nameError}
                />
                <div style={{ display: "flex", flexDirection: "row", alignItems: "center", marginBottom: "10px" }}>
                    <TextField
                        label="이메일"
                        name="email"
                        value={member.email}
                        onChange={onMemberChange}
                        style={{ width: "300px", marginRight: "10px" }}
                        error={!!emailError}
                        placeholder="예: withme@dog.com"
                    />
                    <Button
                        onClick={checkEmailDuplicate}
                        style={{
                            width: "90px",
                            fontSize: "1em",
                            backgroundColor: "#FF7C24",
                            color: "white",
                            height: "56px",
                        }}
                    >
                        중복확인
                    </Button>
                </div>

                {emailMessage && (
                    <Typography style={{ color: emailMessage === "사용 가능한 이메일입니다." ? "green" : "red", margin: "2px auto 8px 0", fontSize: "0.9em" }}>
                        {emailMessage}
                    </Typography>
                )}

                <TextField
                    label="비밀번호"
                    name="password"
                    type="password"
                    value={member.password}
                    onChange={onMemberChange}
                    style={{ width: "400px", marginBottom: "10px" }}
                    placeholder="영문, 숫자, 특수문자 포함 8 ~ 16자"
                    error={!!passwordError}
                    helperText={passwordError}
                />
                <TextField
                    label="비밀번호 확인"
                    name="confirmPassword"
                    type="password"
                    value={member.confirmPassword}
                    onChange={onMemberChange}
                    style={{ width: "400px", marginBottom: "10px" }}
                    error={!!confirmPasswordError}
                    helperText={confirmPasswordError}
                />
                <TextField
                    label="전화번호"
                    name="phone"
                    value={member.phone}
                    onChange={onMemberChange}
                    style={{ width: "400px", marginBottom: "10px" }}
                    error={!!phoneError}
                    helperText={phoneError}
                />
                <TextField
                    label="주소"
                    name="address"
                    value={member.address}
                    onChange={onMemberChange}
                    style={{ width: "400px", marginBottom: "10px" }}
                    placeholder="주소를 입력하세요"
                />
                <Button
                    onClick={handleOnSubmit}
                    variant="contained"
                    style={{
                        width: "400px",
                        height: "50px",
                        backgroundColor: isFormValid ? "#FF7C24" : "#D3D3D3",
                        color: isFormValid ? "white" : "#8B8B8B",
                        fontSize: "1.1em",
                    }}
                    disabled={!isFormValid}
                >
                    회원가입
                </Button>
            </div>
        </div>
    );
}
