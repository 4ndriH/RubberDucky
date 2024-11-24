CREATE TABLE config (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL,
    type TEXT NOT NULL
);

CREATE TABLE users (
    discord_user_id VARCHAR(20) PRIMARY KEY,
    blacklisted BOOLEAN DEFAULT FALSE,
    permissions TEXT DEFAULT NULL
);

CREATE TABLE channel_message_traffic (
    time_stamp TIMESTAMP PRIMARY KEY DEFAULT CURRENT_TIMESTAMP,
    eth_place_bots INT,
    count_thread INT
);

CREATE TABLE place_throughput_log (
    time_stamp TIMESTAMP PRIMARY KEY DEFAULT CURRENT_TIMESTAMP,
    batch_size INT DEFAULT 3600,
    message_batch_time INT
);

CREATE TABLE message_delete_tracker (
    discord_message_id VARCHAR(20) PRIMARY KEY,
    discord_server_id VARCHAR(20),
    discord_channel_id VARCHAR(20),
    time_to_delete TIMESTAMP
);

CREATE TABLE place_projects (
    project_id SERIAL PRIMARY KEY,
    pixels_drawn INT DEFAULT 0,
    discord_user_id VARCHAR(20)
);

CREATE TABLE place_pixels (
    project_id INT,
    index INT,
    x_coordinate INT NOT NULL,
    y_coordinate INT NOT NULL,
    image_color VARCHAR(6),
    alpha REAL DEFAULT 1,
    place_color VARCHAR(6),
    PRIMARY KEY (project_id, index),
    FOREIGN KEY (project_id) REFERENCES place_projects (project_id) ON DELETE CASCADE
);

CREATE TABLE access_pontrol (
    discord_server_id VARCHAR(20) PRIMARY KEY,
    discord_channel_ids TEXT DEFAULT NULL
);
