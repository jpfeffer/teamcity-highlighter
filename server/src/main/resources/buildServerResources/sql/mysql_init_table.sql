use highlighter;
CREATE TABLE hghlt_data (id INT NOT NULL AUTO_INCREMENT,
    build_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    text TEXT,
    level ENUM('info', 'warn', 'error') default 'info',
    block ENUM('collapsed', 'expanded') default 'collapsed',
    ordering ENUM('none', 'alphabet') default 'none',
    ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ID));

CREATE INDEX idx_build_id ON hghlt_data(build_id);