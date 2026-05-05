#!/usr/bin/env python3
import sys
import json
import urllib.request
from datetime import datetime, timezone

if len(sys.argv) < 4:
    print("Usage: ./.github/scripts/discord.py <WEBHOOK_URL> <MODRINTH_ID> <VERSION_NUMBER>")
    sys.exit(1)

webhook_url = sys.argv[1]
modrinth_id = sys.argv[2]
version_number = sys.argv[3]

def fetch_json(url):
    req = urllib.request.Request(url, headers={'User-Agent': 'Playground-Discord-Embed/1.0 (Github-Actions)'})
    with urllib.request.urlopen(req) as res:
        return json.loads(res.read().decode('utf-8'))

try:
    # Gets project details from Modrinth.
    project_data = fetch_json(f"https://api.modrinth.com/v2/project/{modrinth_id}")
    mod_name = project_data.get("title", "Unknown Mod")
    icon_url = project_data.get("icon_url")
    slug = project_data.get("slug")

    # Gets details for specific version.
    versions = fetch_json(f"https://api.modrinth.com/v2/project/{modrinth_id}/version")
    
    # Gets version matching the version_number.
    version_data = next((v for v in versions if v["version_number"] == version_number), None)

    if not version_data:
        print(f"Error: Version {version_number} not found for project {modrinth_id}")
        sys.exit(1)

    version_name = version_data.get("name", f"{mod_name} {version_number}")
    changelog = version_data.get("changelog", "")
    release_type = version_data.get("version_type", "release").lower()
    game_versions = version_data.get("game_versions", [])
    game_versions_str = ", ".join(game_versions)
    version_id = version_data["id"]
    loaders = version_data.get("loaders", [])
    mod_loaders_str = ", ".join(l.capitalize() for l in loaders)
    

except Exception as e:
    print(f"Error fetching data from Modrinth: {e}")
    sys.exit(1)

colors = {
    "release": 3066993,
    "beta": 16753920,
    "alpha": 15548997,
}
color = colors.get(release_type, 3447003)
changelog_truncated = changelog[:2000] + ("..." if len(changelog) > 2000 else "")

payload = {
    "username": f"Modrinth Release",
    "avatar_url": "https://media.beehiiv.com/cdn-cgi/image/fit=scale-down,format=auto,onerror=redirect,quality=80/uploads/publication/logo/a49f8e1b-3835-4ea1-a85b-118c6425ebc3/Modrinth_Dark_Logo.png",
    "embeds": [{
        "title": "",
        "description": f"## {mod_name} {version_number}\n{changelog_truncated}",
        "color": color,
        "thumbnail": { "url": icon_url } if icon_url else None,
        "fields": [
            { "name": "Version", "value": version_number, "inline": True },
            { "name": "Loader", "value": game_versions_str + " (" + mod_loaders_str + ")", "inline": True },
            { "name": "Download", "value": f"[**Modrinth**](https://modrinth.com/mod/{slug}/version/{version_id})", "inline": True }
        ],
        "footer": { "text": "Modrinth Release" },
        "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
    }]
}

headers = {
    'Content-Type': 'application/json',
    'User-Agent': 'Playground/Discord-Embed/1.0 (Github-Actions)'
}
req = urllib.request.Request(webhook_url, data=json.dumps(payload).encode('utf-8'), headers=headers)

try:
    with urllib.request.urlopen(req) as res:
        print(f"Embed sent successfully: {res.status}")
except Exception as e:
    print(f"Failed to send embed: {e}")
    sys.exit(1)
