# FeliUtils Plugin

[![Join Discord](https://img.shields.io/discord/1452733970507763793?style=for-the-badge&label=Join%20Discord)](https://discord.gg/6TgUkMJZw8)
[![GitHub Release](https://img.shields.io/github/v/release/Feli499/FeliUtils-Hytale?style=for-the-badge)](https://github.com/Feli499/FeliUtils-Hytale/releases/latest)

## Overview

FeliUtils is a **technical utility plugin** and **developer tool**. It does **not add gameplay features**, new items,
blocks, or direct changes to the game mechanics. Instead, it works mostly "under the hood" to support the server's
infrastructure.

### Main Features

- **Foundation for Other Plugins:**  
  Acts as a library (API) for other plugins, allowing developers to handle complex tasks without rewriting the same code
  multiple times.

- **Automated Data Management:**  
  Tracks player identification, keeping records of which names belong to which UUIDs, and ensures this information is
  safely stored.

- **Simplifying Backend Development:**  
  Handles player name history and data persistence automatically, allowing developers to focus on game content while
  FeliUtils manages administrative tasks.

In short, FeliUtils is a **developer's toolbox** that provides invisible groundwork, ensuring player data is correctly
managed and serving as a reliable base for other systems.

---

## Supported Features

- **MySQL Support:**
    - Saves player data in MySQL databases.
    - Provides MySQL connection support for dependent plugins if configured in `config.json`, so other plugins do not
      need to create their own SQL connections.

- **Player Info Command (`/pinfo`):**
    - Displays player information:
        - UUID
        - All previous names used to join the server, including join dates
    - **Command Permission:** `feliutils.command.playerinfo`

---

## Installation

1. Place the plugin `.jar` file in your server's `plugins` folder.
2. Start or restart the server to generate the default `config.json`.
3. Configure the MySQL connection (optional) in `config.json` if you want database support.
4. Other plugins can now utilize the library/API functionalities provided by FeliUtils.

---

## Usage for Devs

If you wanna get the last known Player Name or the corresponding UUID for a Player by a name, simply use the
`PlayerDataProviderService` class.

If you want to use the SQL support, you can use the `SQLConnectionService` class.

Otherwise feel free to play around withe the Uitls classes or ask me on my Discord for help.
