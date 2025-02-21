import React, { useState, useEffect } from "react";
import {
  Button,
  Card,
  CardContent,
  Label,
  Typography,
  Box,
  Grid
} from "@mui/material";
import { API_URL } from "../../constant";

// My Page Component (Putting it all together)
const MyPage = () => {
  const [pets, setPets] = useState([]);
  const [selectedPet, setSelectedPet] = useState(null);
  const [isRegistering, setIsRegistering] = useState(false);
  const userId = 1;

  useEffect(() => {
    const fetchPets = async () => {
      try {
        const response = await fetch(`${API_URL}/pets/user/${userId}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error("Failed to fetch pets");
        }

        const data = await response.json();
        setPets(data);
      } catch (error) {
        console.error("Failed to fetch pets", error);
      }
    };

    fetchPets();
  }, [userId]);

  const handlePetRegistered = (newPet) => {
    setPets((prev) => [...prev, newPet]);
    setIsRegistering(false);
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">마이 페이지</h1>

      <div>내정보 수정</div>

      {/* Pet Registration Button */}
      <Button onClick={() => setIsRegistering(true)} className="mb-4">
        펫 등록하기
      </Button>

      {/* Pet Registration Form */}
      {isRegistering && (
        <PetRegistrationForm
          userId={userId}
          onPetRegistered={handlePetRegistered}
        />
      )}

      {/* Pet List Section */}
      <Card className="mb-4">
        <CardHeader>
          <CardTitle>나의 반려동물</CardTitle>
        </CardHeader>
        <CardContent>
          {pets.map((pet) => (
            <div
              key={pet.petId}
              onClick={() => {
                setSelectedPet(pet.petId);
                setIsRegistering(false);
              }}
              className="cursor-pointer p-2 hover:bg-gray-100 border-b">
              {pet.name} - {pet.breed}
            </div>
          ))}
        </CardContent>
      </Card>

      {/* Pet Details Section */}
      {selectedPet && <PetDetailsView petId={selectedPet} />}
    </div>
  );
};

export default PetPage;