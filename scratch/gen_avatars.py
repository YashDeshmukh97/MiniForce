import os

svg_base = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><rect width='100' height='100' fill='{bg}'/>{content}</svg>"

designs = {
    "avatar_default": {"bg": "#21262d", "c": "<circle cx='50' cy='35' r='18' fill='#6e7681'/><path d='M15 100 C15 60 85 60 85 100' fill='#6e7681'/>"},
    "robot_01": {"bg": "#1f2937", "c": "<rect x='25' y='25' width='50' height='50' rx='8' fill='#4b5563'/><circle cx='35' cy='40' r='6' fill='#60a5fa'/><circle cx='65' cy='40' r='6' fill='#60a5fa'/><rect x='35' y='60' width='30' height='5' fill='#9ca3af'/>"},
    "cat_01": {"bg": "#374151", "c": "<polygon points='20,20 40,40 20,50' fill='#fca5a5'/><polygon points='80,20 60,40 80,50' fill='#fca5a5'/><circle cx='50' cy='60' r='35' fill='#9ca3af'/><circle cx='35' cy='55' r='5' fill='#1f2937'/><circle cx='65' cy='55' r='5' fill='#1f2937'/><polygon points='45,65 55,65 50,75' fill='#fca5a5'/>"},
    "fox_01": {"bg": "#7c2d12", "c": "<polygon points='10,20 40,50 10,60' fill='#ea580c'/><polygon points='90,20 60,50 90,60' fill='#ea580c'/><polygon points='10,60 50,95 90,60' fill='#f97316'/><polygon points='30,60 50,95 70,60' fill='#fffedd'/>"},
    "panda_01": {"bg": "#064e3b", "c": "<circle cx='25' cy='25' r='15' fill='#111827'/><circle cx='75' cy='25' r='15' fill='#111827'/><circle cx='50' cy='60' r='40' fill='#f3f4f6'/><circle cx='35' cy='50' r='12' fill='#111827'/><circle cx='65' cy='50' r='12' fill='#111827'/><circle cx='50' cy='70' r='8' fill='#111827'/>"},
    "lion_01": {"bg": "#78350f", "c": "<circle cx='50' cy='50' r='45' fill='#d97706'/><circle cx='50' cy='55' r='30' fill='#fcd34d'/><circle cx='35' cy='45' r='5' fill='#451a03'/><circle cx='65' cy='45' r='5' fill='#451a03'/><polygon points='45,60 55,60 50,70' fill='#451a03'/>"},
    "frog_01": {"bg": "#064e3b", "c": "<circle cx='30' cy='35' r='15' fill='#22c55e'/><circle cx='70' cy='35' r='15' fill='#22c55e'/><ellipse cx='50' cy='65' rx='45' ry='30' fill='#16a34a'/><circle cx='30' cy='35' r='5' fill='#022c22'/><circle cx='70' cy='35' r='5' fill='#022c22'/><path d='M30 70 Q50 85 70 70' stroke='#022c22' stroke-width='4' fill='none'/>"},
    "koala_01": {"bg": "#831843", "c": "<circle cx='20' cy='40' r='18' fill='#9ca3af'/><circle cx='80' cy='40' r='18' fill='#9ca3af'/><ellipse cx='50' cy='60' rx='40' ry='35' fill='#d1d5db'/><ellipse cx='50' cy='65' rx='12' ry='18' fill='#374151'/><circle cx='35' cy='50' r='4' fill='#111827'/><circle cx='65' cy='50' r='4' fill='#111827'/>"},
    "tiger_01": {"bg": "#451a03", "c": "<circle cx='50' cy='55' r='40' fill='#f97316'/><polygon points='10,10 35,35 15,45' fill='#ea580c'/><polygon points='90,10 65,35 85,45' fill='#ea580c'/><path d='M50 15 L45 35 L55 35 Z' fill='#111827'/><path d='M20 50 L35 45 L35 55 Z' fill='#111827'/><path d='M80 50 L65 45 L65 55 Z' fill='#111827'/><polygon points='45,65 55,65 50,75' fill='#111827'/>"},
    "monkey_01": {"bg": "#14532d", "c": "<circle cx='20' cy='50' r='15' fill='#b45309'/><circle cx='80' cy='50' r='15' fill='#b45309'/><circle cx='50' cy='55' r='35' fill='#92400e'/><ellipse cx='50' cy='65' rx='25' ry='20' fill='#fef3c7'/><circle cx='40' cy='45' r='5' fill='#111827'/><circle cx='60' cy='45' r='5' fill='#111827'/><path d='M40 70 Q50 80 60 70' stroke='#111827' stroke-width='3' fill='none'/>"},
    "unicorn_01": {"bg": "#4c1d95", "c": "<polygon points='50,10 40,40 60,40' fill='#fde047'/><circle cx='50' cy='65' r='35' fill='#fdf4ff'/><circle cx='35' cy='55' r='5' fill='#86198f'/><circle cx='65' cy='55' r='5' fill='#86198f'/><path d='M20 70 Q50 40 80 70' stroke='#d946ef' stroke-width='4' fill='none'/>"},
    "developer_01": {"bg": "#0f172a", "c": "<rect x='20' y='30' width='60' height='45' rx='4' fill='#334155'/><path d='M30 45 L40 52.5 L30 60' stroke='#10b981' stroke-width='4' fill='none'/><line x1='45' y1='60' x2='60' y2='60' stroke='#e2e8f0' stroke-width='4'/>"},
    "gaming_01": {"bg": "#312e81", "c": "<rect x='15' y='35' width='70' height='40' rx='20' fill='#6366f1'/><circle cx='35' cy='50' r='10' fill='#c7d2fe'/><line x1='35' y1='43' x2='35' y2='57' stroke='#4338ca' stroke-width='4'/><line x1='28' y1='50' x2='42' y2='50' stroke='#4338ca' stroke-width='4'/><circle cx='60' cy='55' r='5' fill='#fca5a5'/><circle cx='70' cy='45' r='5' fill='#86efac'/>"},
    "rocket_01": {"bg": "#1e3a8a", "c": "<polygon points='50,15 30,55 70,55' fill='#ef4444'/><rect x='35' y='55' width='30' height='25' fill='#cbd5e1'/><polygon points='35,65 20,80 35,80' fill='#ef4444'/><polygon points='65,65 80,80 65,80' fill='#ef4444'/><circle cx='50' cy='65' r='6' fill='#1e3a8a'/><polygon points='40,80 60,80 50,95' fill='#f59e0b'/>"},
    "lightning_01": {"bg": "#111827", "c": "<polygon points='60,10 30,50 50,50 40,90 70,45 50,45' fill='#eab308'/>"},
    "terminal_01": {"bg": "#022c22", "c": "<path d='M25 35 L40 50 L25 65' stroke='#10b981' stroke-width='6' fill='none'/><line x1='45' y1='65' x2='75' y2='65' stroke='#10b981' stroke-width='6'/>"},
    "controller_01": {"bg": "#581c87", "c": "<path d='M15 40 Q50 20 85 40 L90 70 Q50 80 10 70 Z' fill='#d8b4fe'/><circle cx='30' cy='50' r='10' fill='#7e22ce'/><line x1='30' y1='44' x2='30' y2='56' stroke='#f3e8ff' stroke-width='3'/><line x1='24' y1='50' x2='36' y2='50' stroke='#f3e8ff' stroke-width='3'/><circle cx='65' cy='55' r='4' fill='#f43f5e'/><circle cx='75' cy='45' r='4' fill='#3b82f6'/>"}
}

output_dir = "frontend/assets/avatars"
os.makedirs(output_dir, exist_ok=True)

for name, data in designs.items():
    content = svg_base.format(bg=data["bg"], content=data["c"])
    with open(f"{output_dir}/{name}.svg", "w", encoding='utf-8') as f:
        f.write(content)

print("Created 17 avatar SVGs successfully.")
