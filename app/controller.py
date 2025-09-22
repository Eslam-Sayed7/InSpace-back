# Logic of the API endpoints

import pyautogui

def perform_left_click(x: int, y: int):
    pyautogui.click(x=x, y=y, button="left")
    return {"status": "left clicked", "x": x, "y": y}

def perform_right_click(x: int, y: int):
    pyautogui.click(x=x, y=y, button="right")
    return {"status": "right clicked", "x": x, "y": y}

def perform_double_click(x: int, y: int):
    pyautogui.doubleClick(x=x, y=y, button="left")
    return {"status": "double clicked", "x": x, "y": y}

def perform_move(x: int, y: int):
    pyautogui.moveTo(x, y)
    return {"status": "moved", "x": x, "y": y}

def perform_scroll_up(amount: int):
    pyautogui.scroll(amount)  # positive = up
    return {"status": "scrolled up", "amount": amount}

def perform_scroll_down(amount: int):
    pyautogui.scroll(-amount)  # negative = down
    return {"status": "scrolled down", "amount": amount}

def perform_write(text: str):
    pyautogui.write(text)
    return {"status": "written", "text": text}

def perform_keypress(key: str):
    pyautogui.press(key)
    return {"status": "key pressed", "key": key}

def perform_hotkey(keys: list[str]):
    pyautogui.hotkey(*keys)
    return {"status": "hotkey pressed", "keys": keys}