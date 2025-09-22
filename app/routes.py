from fastapi import APIRouter
from .schemas import *
from .controller import *

router = APIRouter()

@router.post("/leftclick")
def left_click(request: ClickRequest):
    return perform_left_click(request.x, request.y)

@router.post("/rightclick")
def right_click(request: ClickRequest):
    return perform_right_click(request.x, request.y)

@router.post("/doubleclick")
def double_click(request: ClickRequest):
    return perform_double_click(request.x, request.y)

@router.post("/move")
def move(request: ClickRequest):
    return perform_move(request.x, request.y)

@router.post("/scroll/up")
def scroll_up(request: ScrollRequest):
    return perform_scroll_up(request.amount)
    
@router.post("/scroll/down")
def scroll_down(request: ScrollRequest):
    return perform_scroll_down(request.amount)

@router.post("/write")
def write_text(request: WriteRequest):
    return perform_write(request.text)

@router.post("/keypress")
def keypress(request: KeyPressRequest):
    return perform_keypress(request.key)

@router.post("/hotkey")
def hotkey(request: HotkeyRequest):
    return perform_hotkey(request.keys)