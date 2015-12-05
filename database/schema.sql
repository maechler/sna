CREATE TABLE IF NOT EXISTS edges (
  'from' long NOT NULL,
  'to' long NOT NULL,
  PRIMARY KEY ('from', 'to')
);

CREATE TABLE IF NOT EXISTS nodes (
  'id' long PRIMARY KEY NOT NULL,
  'screenName' VARCHAR(255) NOT NULL,
  'followersCount' int unsigned NOT NULL,
  'followingsCount' int unsigned NOT NULL,
  'tweetsCount' int unsigned NOT NULL,
  'description' TEXT NOT NULL,
  'name' VARCHAR(255) NOT NULL,
  'location' VARCHAR(255) NOT NULL,
  'lang' VARCHAR(255) NOT NULL,
  'type' VARCHAR(255) NOT NULL,
  'loadedFollowers' int DEFAULT 0 NOT NULL
);