"""Unit tests for FastAPI routes with mocked controller functions."""
import pytest
from unittest.mock import patch
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


class TestClickRoutes:
    """Test click-related routes."""

    @patch('app.routes.perform_left_click')
    def test_left_click_route(self, mock_perform_left_click):
        """Test left click route calls controller correctly."""
        # Arrange
        mock_perform_left_click.return_value = {"status": "left clicked", "x": 100, "y": 200}

        # Act
        response = client.post("/leftclick", json={"x": 100, "y": 200})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "left clicked", "x": 100, "y": 200}
        mock_perform_left_click.assert_called_once_with(100, 200)

    @patch('app.routes.perform_right_click')
    def test_right_click_route(self, mock_perform_right_click):
        """Test right click route calls controller correctly."""
        # Arrange
        mock_perform_right_click.return_value = {"status": "right clicked", "x": 150, "y": 250}

        # Act
        response = client.post("/rightclick", json={"x": 150, "y": 250})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "right clicked", "x": 150, "y": 250}
        mock_perform_right_click.assert_called_once_with(150, 250)

    @patch('app.routes.perform_double_click')
    def test_double_click_route(self, mock_perform_double_click):
        """Test double click route calls controller correctly."""
        # Arrange
        mock_perform_double_click.return_value = {"status": "double clicked", "x": 300, "y": 400}

        # Act
        response = client.post("/doubleclick", json={"x": 300, "y": 400})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "double clicked", "x": 300, "y": 400}
        mock_perform_double_click.assert_called_once_with(300, 400)


class TestMouseMovementRoutes:
    """Test mouse movement routes."""

    @patch('app.routes.perform_move')
    def test_move_route(self, mock_perform_move):
        """Test move route calls controller correctly."""
        # Arrange
        mock_perform_move.return_value = {"status": "moved", "x": 500, "y": 600}

        # Act
        response = client.post("/move", json={"x": 500, "y": 600})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "moved", "x": 500, "y": 600}
        mock_perform_move.assert_called_once_with(500, 600)


class TestScrollRoutes:
    """Test scroll-related routes."""

    @patch('app.routes.perform_scroll_up')
    def test_scroll_up_route(self, mock_perform_scroll_up):
        """Test scroll up route calls controller correctly."""
        # Arrange
        mock_perform_scroll_up.return_value = {"status": "scrolled up", "amount": 10}

        # Act
        response = client.post("/scroll/up", json={"amount": 10})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "scrolled up", "amount": 10}
        mock_perform_scroll_up.assert_called_once_with(10)

    @patch('app.routes.perform_scroll_down')
    def test_scroll_down_route(self, mock_perform_scroll_down):
        """Test scroll down route calls controller correctly."""
        # Arrange
        mock_perform_scroll_down.return_value = {"status": "scrolled down", "amount": 5}

        # Act
        response = client.post("/scroll/down", json={"amount": 5})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "scrolled down", "amount": 5}
        mock_perform_scroll_down.assert_called_once_with(5)


class TestKeyboardRoutes:
    """Test keyboard-related routes."""

    @patch('app.routes.perform_write')
    def test_write_route(self, mock_perform_write):
        """Test write route calls controller correctly."""
        # Arrange
        mock_perform_write.return_value = {"status": "written", "text": "Hello World"}

        # Act
        response = client.post("/write", json={"text": "Hello World"})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "written", "text": "Hello World"}
        mock_perform_write.assert_called_once_with("Hello World")

    @patch('app.routes.perform_keypress')
    def test_keypress_route(self, mock_perform_keypress):
        """Test keypress route calls controller correctly."""
        # Arrange
        mock_perform_keypress.return_value = {"status": "key pressed", "key": "enter"}

        # Act
        response = client.post("/keypress", json={"key": "enter"})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "key pressed", "key": "enter"}
        mock_perform_keypress.assert_called_once_with("enter")

    @patch('app.routes.perform_hotkey')
    def test_hotkey_route(self, mock_perform_hotkey):
        """Test hotkey route calls controller correctly."""
        # Arrange
        mock_perform_hotkey.return_value = {"status": "hotkey pressed", "keys": ["ctrl", "c"]}

        # Act
        response = client.post("/hotkey", json={"keys": ["ctrl", "c"]})

        # Assert
        assert response.status_code == 200
        assert response.json() == {"status": "hotkey pressed", "keys": ["ctrl", "c"]}
        mock_perform_hotkey.assert_called_once_with(["ctrl", "c"])


class TestValidation:
    """Test request validation."""

    def test_left_click_missing_x_coordinate(self):
        """Test left click with missing x coordinate returns validation error."""
        # Act
        response = client.post("/leftclick", json={"y": 200})

        # Assert
        assert response.status_code == 422

    def test_left_click_missing_y_coordinate(self):
        """Test left click with missing y coordinate returns validation error."""
        # Act
        response = client.post("/leftclick", json={"x": 100})

        # Assert
        assert response.status_code == 422

    def test_scroll_up_missing_amount(self):
        """Test scroll up with missing amount returns validation error."""
        # Act
        response = client.post("/scroll/up", json={})

        # Assert
        assert response.status_code == 422

    def test_write_missing_text(self):
        """Test write with missing text returns validation error."""
        # Act
        response = client.post("/write", json={})

        # Assert
        assert response.status_code == 422

    def test_keypress_missing_key(self):
        """Test keypress with missing key returns validation error."""
        # Act
        response = client.post("/keypress", json={})

        # Assert
        assert response.status_code == 422

    def test_hotkey_missing_keys(self):
        """Test hotkey with missing keys returns validation error."""
        # Act
        response = client.post("/hotkey", json={})

        # Assert
        assert response.status_code == 422


class TestHomeRoute:
    """Test home route."""

    def test_home_route(self):
        """Test home route returns expected message."""
        # Act
        response = client.get("/")

        # Assert
        assert response.status_code == 200
        assert response.json() == {"message": "UI Simulation Server is running"}
