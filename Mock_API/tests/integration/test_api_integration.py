"""Integration tests for the FastAPI application with mocked pyautogui."""
import pytest
from unittest.mock import patch
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


class TestClickIntegration:
    """Integration tests for click operations."""

    @patch('app.controller.pyautogui')
    def test_left_click_end_to_end(self, mock_pyautogui):
        """Test left click from route through to controller."""
        # Arrange
        payload = {"x": 100, "y": 200}

        # Act
        response = client.post("/leftclick", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "left clicked"
        assert data["x"] == 100
        assert data["y"] == 200
        mock_pyautogui.click.assert_called_once_with(x=100, y=200, button="left")

    @patch('app.controller.pyautogui')
    def test_right_click_end_to_end(self, mock_pyautogui):
        """Test right click from route through to controller."""
        # Arrange
        payload = {"x": 150, "y": 250}

        # Act
        response = client.post("/rightclick", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "right clicked"
        assert data["x"] == 150
        assert data["y"] == 250
        mock_pyautogui.click.assert_called_once_with(x=150, y=250, button="right")

    @patch('app.controller.pyautogui')
    def test_double_click_end_to_end(self, mock_pyautogui):
        """Test double click from route through to controller."""
        # Arrange
        payload = {"x": 300, "y": 400}

        # Act
        response = client.post("/doubleclick", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "double clicked"
        assert data["x"] == 300
        assert data["y"] == 400
        mock_pyautogui.doubleClick.assert_called_once_with(x=300, y=400, button="left")


class TestMouseMovementIntegration:
    """Integration tests for mouse movement operations."""

    @patch('app.controller.pyautogui')
    def test_move_end_to_end(self, mock_pyautogui):
        """Test mouse move from route through to controller."""
        # Arrange
        payload = {"x": 500, "y": 600}

        # Act
        response = client.post("/move", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "moved"
        assert data["x"] == 500
        assert data["y"] == 600
        mock_pyautogui.moveTo.assert_called_once_with(500, 600)


class TestScrollIntegration:
    """Integration tests for scroll operations."""

    @patch('app.controller.pyautogui')
    def test_scroll_up_end_to_end(self, mock_pyautogui):
        """Test scroll up from route through to controller."""
        # Arrange
        payload = {"amount": 10}

        # Act
        response = client.post("/scroll/up", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "scrolled up"
        assert data["amount"] == 10
        mock_pyautogui.scroll.assert_called_once_with(10)

    @patch('app.controller.pyautogui')
    def test_scroll_down_end_to_end(self, mock_pyautogui):
        """Test scroll down from route through to controller."""
        # Arrange
        payload = {"amount": 5}

        # Act
        response = client.post("/scroll/down", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "scrolled down"
        assert data["amount"] == 5
        mock_pyautogui.scroll.assert_called_once_with(-5)


class TestKeyboardIntegration:
    """Integration tests for keyboard operations."""

    @patch('app.controller.pyautogui')
    def test_write_end_to_end(self, mock_pyautogui):
        """Test write from route through to controller."""
        # Arrange
        payload = {"text": "Hello World"}

        # Act
        response = client.post("/write", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "written"
        assert data["text"] == "Hello World"
        mock_pyautogui.write.assert_called_once_with("Hello World")

    @patch('app.controller.pyautogui')
    def test_keypress_end_to_end(self, mock_pyautogui):
        """Test keypress from route through to controller."""
        # Arrange
        payload = {"key": "enter"}

        # Act
        response = client.post("/keypress", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "key pressed"
        assert data["key"] == "enter"
        mock_pyautogui.press.assert_called_once_with("enter")

    @patch('app.controller.pyautogui')
    def test_hotkey_end_to_end(self, mock_pyautogui):
        """Test hotkey from route through to controller."""
        # Arrange
        payload = {"keys": ["ctrl", "c"]}

        # Act
        response = client.post("/hotkey", json=payload)

        # Assert
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "hotkey pressed"
        assert data["keys"] == ["ctrl", "c"]
        mock_pyautogui.hotkey.assert_called_once_with("ctrl", "c")


class TestMultipleOperations:
    """Integration tests for multiple operations in sequence."""

    @patch('app.controller.pyautogui')
    def test_multiple_clicks_sequence(self, mock_pyautogui):
        """Test multiple click operations in sequence."""
        # Act - Perform left click
        response1 = client.post("/leftclick", json={"x": 10, "y": 20})
        # Act - Perform right click
        response2 = client.post("/rightclick", json={"x": 30, "y": 40})

        # Assert
        assert response1.status_code == 200
        assert response2.status_code == 200
        assert mock_pyautogui.click.call_count == 2

    @patch('app.controller.pyautogui')
    def test_click_and_write_sequence(self, mock_pyautogui):
        """Test click followed by write operation."""
        # Act
        response1 = client.post("/leftclick", json={"x": 100, "y": 200})
        response2 = client.post("/write", json={"text": "Test"})

        # Assert
        assert response1.status_code == 200
        assert response2.status_code == 200
        mock_pyautogui.click.assert_called_once_with(x=100, y=200, button="left")
        mock_pyautogui.write.assert_called_once_with("Test")


class TestErrorHandling:
    """Integration tests for error handling."""

    def test_invalid_json_payload(self):
        """Test invalid JSON payload returns error."""
        # Act
        response = client.post(
            "/leftclick",
            content="invalid json",
            headers={"Content-Type": "application/json"}
        )

        # Assert
        assert response.status_code == 422

    def test_invalid_data_type_for_coordinates(self):
        """Test invalid data type for coordinates returns validation error."""
        # Act
        response = client.post("/leftclick", json={"x": "invalid", "y": 200})

        # Assert
        assert response.status_code == 422

    def test_negative_coordinates(self):
        """Test negative coordinates are accepted (valid use case)."""
        # Arrange
        with patch('app.controller.pyautogui') as mock_pyautogui:
            # Act
            response = client.post("/leftclick", json={"x": -10, "y": -20})

            # Assert
            assert response.status_code == 200
            mock_pyautogui.click.assert_called_once_with(x=-10, y=-20, button="left")


class TestAPIHealthCheck:
    """Integration tests for API health check."""

    def test_home_endpoint(self):
        """Test home endpoint returns server status."""
        # Act
        response = client.get("/")

        # Assert
        assert response.status_code == 200
        assert response.json() == {"message": "UI Simulation Server is running"}
