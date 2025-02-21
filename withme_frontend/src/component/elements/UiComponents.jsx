import React, { useState } from "react";
import {
  PrimaryButton,
  OutlineButton,
  ToggleButton,
  FloatingActionButton,
  DeleteButton,
  TextButton,
  CustomTextField,
  CustomCheckbox,
  CustomRadioButton,
  CustomDialog,
  CustomTooltip,
  CustomSnackbar
} from "./CustomComponents";
import { Checkbox, Input, Radio } from "@mui/material";

const UiComponents = () => {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [snackbarOpen, setSnackbarOpen] = useState(false);

  const handleDialogOpen = () => setDialogOpen(true);
  const handleDialogClose = () => setDialogOpen(false);

  const handleSnackbarOpen = () => setSnackbarOpen(true);
  const handleSnackbarClose = () => setSnackbarOpen(false);

  return (
    <div style={{ padding: "20px" }}>
      <h1>Custom Buttons</h1>
      <PrimaryButton onClick={handleDialogOpen}>Open Dialog</PrimaryButton>
      <OutlineButton onClick={handleSnackbarOpen}>Show Snackbar</OutlineButton>
      <TextButton style={{ borderRadius: "50px" }}>Text Button</TextButton>
      <ToggleButton isActive={true}>Toggle</ToggleButton>
      <FloatingActionButton>+</FloatingActionButton>
      <DeleteButton>Delete Button</DeleteButton>
      <h1>Custom Input</h1>
      <CustomTextField label="Enter Text" variant="outlined" />
      <Input aria-label="Demo input" placeholder="Type something…" />
      <h2>Checkbox</h2>
      <CustomCheckbox /> <span>커스텀</span>
      <Checkbox />
      <span>기본</span>
      <h2>Radio Button</h2>
      <CustomRadioButton />
      <span>커스텀</span>
      <Radio />
      <span>기본</span>
      <br />
      {/* Dialog */}
      <CustomDialog open={dialogOpen} onClose={handleDialogClose}>
        <div>
          <h2>Custom Dialog</h2>
          <p>This is a custom dialog.</p>
          <PrimaryButton onClick={handleDialogClose}>Close</PrimaryButton>
        </div>
      </CustomDialog>
      {/* Tooltip */}
      <CustomTooltip title="This is a tooltip">
        <span style={{ cursor: "pointer", textDecoration: "underline" }}>
          Hover over me
        </span>
      </CustomTooltip>
      {/* Snackbar */}
      <CustomSnackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={handleSnackbarClose}
        message="This is a custom snackbar!"
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
      />
    </div>
  );
};

export default UiComponents;
