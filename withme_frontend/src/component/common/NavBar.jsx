import { NavLink } from "react-router-dom";
import React, { useState } from "react";
import "../../assets/css/common/NavBar.css";

const NavBar = () => {
  const [hovered, setHovered] = useState(null);

  return (
    <nav className="nav-bar">
      {[
        { name: "홈", path: "/" },
        { name: "쇼핑몰", path: "/item/list" },
        { name: "공지사항", path: "/notices" },
        { name: "커뮤니티", path: "/posts" },
      ].map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          onMouseEnter={() => setHovered(item.path)}
          onMouseLeave={() => setHovered(null)}
        >
          {item.name}
        </NavLink>
      ))}
    </nav>
  );
};

export default NavBar;