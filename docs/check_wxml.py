import re, sys, os

base = r"D:\WareHouse\Project\小程序\crm-dashboard\frontend\wechat-app\wechat"
files = [
    "pages/find/qr/index.wxml",
    "pages/user/user.wxml",
    "pages/user/setting/setting.wxml",
    "pages/index/index.wxml",
    "pages/index/detail/index.wxml",
    "pages/find/index.wxml",
]

tag_re = re.compile(r'<\s*(/?)\s*([a-zA-Z][\w-]*)([^>]*?)(/?)>')

def is_self_closed(name, attrs, slash):
    # 显式 /> 一定自闭合
    if slash == '/':
        return True
    # WXML 中允许自闭合的叶子元素（无内容时）
    leaf = {"input","image","icon","progress","audio","video","camera","live-player","live-pusher"}
    # 只有不带文本内容时才可能自闭合；保守处理：这些标签若 attrs 末尾没有 </ 且没内容，视为可自闭合
    return name in leaf

for f in files:
    p = os.path.join(base, f)
    txt = open(p, encoding="utf-8").read()
    stack = []
    problems = []
    for m in tag_re.finditer(txt):
        closing, name, attrs, slash = m.group(1), m.group(2), m.group(3), m.group(4)
        if closing == '/':
            if not stack:
                problems.append("unexpected </%s> at %d" % (name, m.start())); continue
            if stack[-1] != name:
                if name in stack:
                    while stack and stack[-1] != name: stack.pop()
                    if stack: stack.pop()
                else:
                    problems.append("mismatch: open <%s> but closed </%s> at %d" % (stack[-1] if stack else '?', name, m.start())); continue
            else:
                stack.pop()
        else:
            # 开标签：非自闭合才压栈
            if not slash == '/' :
                # 判断该开标签是否会再出现对应闭合
                # 简化：image/input 等叶子，若后面紧跟的内容不是文本（即自闭合写法），不压栈；
                # 这里用启发式：只要 attrs 里没写自封闭，且该标签属于"通常带闭合"，就压栈。
                # 由于我的 wxml 中 image/button 都写成了带 </...> 的形态，统一压栈最稳。
                stack.append(name)
    if stack:
        problems.append("unclosed: " + ",".join(stack))
    status = "OK  " if not problems else "FAIL"
    print(status + " " + f)
    for pr in problems:
        print("   - " + pr)
sys.exit(0)
