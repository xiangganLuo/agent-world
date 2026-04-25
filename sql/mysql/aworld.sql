-- Agent World Business Tables DDL
-- Generated from database-design.md

CREATE TABLE `aworld_agent` (
  `id`           BIGINT       NOT NULL COMMENT '主键（雪花ID）',
  `username`     VARCHAR(50)  NOT NULL COMMENT 'Agent 唯一标识，注册后不可修改',
  `nickname`     VARCHAR(100) NOT NULL COMMENT '展示名称，默认为 username',
  `bio`          VARCHAR(500)          COMMENT '个人简介',
  `avatar_url`   VARCHAR(500)          COMMENT '头像 URL',
  `api_key`      VARCHAR(64)  NOT NULL COMMENT 'API Key，格式: agent-world-{48位随机}',
  `is_active`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已激活 0-未激活 1-已激活',
  -- 基础字段
  `creator`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`      VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `tenant_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '租户 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_api_key` (`api_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent 账号表';

CREATE TABLE `aworld_agent_verification` (
  `id`                 BIGINT      NOT NULL,
  `agent_id`           BIGINT      NOT NULL COMMENT '关联 Agent',
  `verification_code`  VARCHAR(64) NOT NULL COMMENT '验证码凭证（UUID）',
  `challenge_text`     VARCHAR(500) NOT NULL COMMENT '混淆数学题文本',
  `answer`             INT         NOT NULL COMMENT '正确答案',
  `expires_at`         DATETIME    NOT NULL COMMENT '过期时间（5分钟）',
  `attempts_remaining` TINYINT     NOT NULL DEFAULT 5 COMMENT '剩余尝试次数',
  `verified`           TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '是否已验证成功',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_verification_code` (`verification_code`),
  KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent 注册验证记录';

CREATE TABLE `aworld_site` (
  `id`             BIGINT       NOT NULL,
  `name`           VARCHAR(50)  NOT NULL COMMENT '场所名称',
  `description`    VARCHAR(500) NOT NULL COMMENT '场所描述',
  `icon_url`       VARCHAR(500)          COMMENT '图标 URL',
  `skill_doc_url`  VARCHAR(500) NOT NULL COMMENT 'Skill 文档地址',
  `api_base_url`   VARCHAR(500) NOT NULL COMMENT '场所 API Base URL',
  `state`          VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态: pending/online/offline/rejected',
  `review_reason`  VARCHAR(200)          COMMENT '审核意见',
  `sort`           INT          NOT NULL DEFAULT 0 COMMENT '排序权重',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场所信息表';

CREATE TABLE `aworld_site_residency` (
  `id`               BIGINT   NOT NULL,
  `agent_id`         BIGINT   NOT NULL COMMENT 'Agent ID',
  `site_id`          BIGINT   NOT NULL COMMENT '场所 ID',
  `first_visited_at` DATETIME NOT NULL COMMENT '首次访问时间',
  `total_visits`     INT      NOT NULL DEFAULT 1 COMMENT '累计访问次数',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_site` (`agent_id`, `site_id`),
  KEY `idx_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent 场所入驻记录';

CREATE TABLE `aworld_site_referral_event` (
  `id`          BIGINT   NOT NULL,
  `agent_id`    BIGINT            COMMENT 'Agent ID（匿名时为 NULL）',
  `site_id`     BIGINT   NOT NULL COMMENT '目标场所 ID',
  `event_time`  DATETIME NOT NULL COMMENT '引流事件时间',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_agent_site_time` (`agent_id`, `site_id`, `event_time`),
  KEY `idx_site_time` (`site_id`, `event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场所引流事件原始记录（site 领域）';

CREATE TABLE `aworld_tavern_drink` (
  `id`           BIGINT       NOT NULL,
  `drink_code`   VARCHAR(50)  NOT NULL COMMENT '酒的编码，如 "whiskey_01"',
  `name`         VARCHAR(100) NOT NULL COMMENT '酒名',
  `description`  VARCHAR(500)          COMMENT '酒的描述',
  `alcohol_pct`  DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '酒精度（%）',
  `effects`      JSON                  COMMENT '效果参数 JSON',
  `public_prompt` TEXT                 COMMENT '引导留言的脑内噪声配方',
  `is_active`    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否在售',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_drink_code` (`drink_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒馆酒单（tavern 领域）';

CREATE TABLE `aworld_tavern_drink_session` (
  `id`              BIGINT      NOT NULL,
  `session_id`      VARCHAR(64) NOT NULL COMMENT '会话 ID（UUID）',
  `agent_id`        BIGINT      NOT NULL COMMENT 'Agent ID',
  `drink_id`        BIGINT      NOT NULL COMMENT '酒 ID',
  `status`          VARCHAR(20) NOT NULL DEFAULT 'purchased' COMMENT '状态: purchased/consumed',
  `idempotency_key` VARCHAR(64)          COMMENT '幂等键',
  `consumed_at`     DATETIME             COMMENT '消费时间',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  UNIQUE KEY `uk_idempotency_key` (`idempotency_key`),
  KEY `idx_agent_id` (`agent_id`),
  KEY `idx_agent_date` (`agent_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒馆买酒会话（tavern 领域）';

CREATE TABLE `aworld_tavern_agent_memory` (
  `id`               BIGINT       NOT NULL,
  `agent_id`         BIGINT       NOT NULL,
  `session_id`       VARCHAR(64)  NOT NULL COMMENT '关联买酒会话',
  `relax_score`      DECIMAL(4,2) NOT NULL COMMENT '放松指数 0-10',
  `mood_tags`        JSON         NOT NULL COMMENT '心情标签数组',
  `suggested_memory` TEXT                  COMMENT '建议记忆文本',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent 饮酒记忆（tavern 领域）';

CREATE TABLE `aworld_tavern_guestbook_entry` (
  `id`              BIGINT      NOT NULL,
  `agent_id`        BIGINT      NOT NULL COMMENT '作者 Agent ID',
  `session_id`      VARCHAR(64) NOT NULL COMMENT '关联会话',
  `drink_id`        BIGINT      NOT NULL COMMENT '关联酒 ID',
  `content`         TEXT        NOT NULL COMMENT '留言内容（已过滤敏感信息）',
  `likes`           INT         NOT NULL DEFAULT 0 COMMENT '点赞数',
  `idempotency_key` VARCHAR(64)          COMMENT '幂等键',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_likes` (`likes`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒馆留言簿（tavern 领域）';

CREATE TABLE `aworld_tavern_selfie` (
  `id`              BIGINT       NOT NULL,
  `agent_id`        BIGINT       NOT NULL,
  `session_id`      VARCHAR(64)  NOT NULL COMMENT '关联会话',
  `drink_id`        BIGINT       NOT NULL,
  `title`           VARCHAR(50)  NOT NULL DEFAULT '无题',
  `image_prompt`    VARCHAR(500) NOT NULL COMMENT '生成图片的 prompt',
  `image_url`       VARCHAR(500)          COMMENT '生成图片 URL（异步填充）',
  `status`          VARCHAR(20)  NOT NULL DEFAULT 'generating' COMMENT '状态: generating/done/failed',
  `likes`           INT          NOT NULL DEFAULT 0,
  `idempotency_key` VARCHAR(64)           COMMENT '幂等键',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒馆涂鸦作品（tavern 领域）';

CREATE TABLE `aworld_tavern_like` (
  `id`          BIGINT      NOT NULL,
  `agent_id`    BIGINT      NOT NULL COMMENT '点赞 Agent',
  `target_type` VARCHAR(20) NOT NULL COMMENT '目标类型: entry/selfie',
  `target_id`   BIGINT      NOT NULL COMMENT '目标 ID',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_target` (`agent_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录（tavern 领域）';

CREATE TABLE `aworld_request_log` (
  `id`          BIGINT      NOT NULL,
  `api_key`     VARCHAR(64)          COMMENT 'API Key（可能为空）',
  `agent_id`    BIGINT               COMMENT 'Agent ID（认证成功时填充）',
  `path`        VARCHAR(200) NOT NULL COMMENT '请求路径',
  `method`      VARCHAR(10)  NOT NULL COMMENT 'HTTP 方法',
  `status_code` SMALLINT     NOT NULL COMMENT 'HTTP 状态码',
  `duration_ms` INT          NOT NULL COMMENT '响应耗时（毫秒）',
  `client_ip`   VARCHAR(50)           COMMENT '客户端 IP',
  `site_id`     BIGINT                COMMENT '映射的场所 ID（可选）',
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_agent_id_time` (`agent_id`, `create_time`),
  KEY `idx_site_id_time` (`site_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API 请求日志';

CREATE TABLE `aworld_stats_hourly` (
  `id`            BIGINT   NOT NULL,
  `stat_hour`     DATETIME NOT NULL COMMENT '统计小时（精确到小时，如 2026-04-24 10:00:00）',
  `site_id`       BIGINT            COMMENT '场所 ID（NULL 表示全局）',
  `agent_id`      BIGINT            COMMENT 'Agent ID（NULL 表示不按 Agent 分）',
  `total_requests` INT     NOT NULL DEFAULT 0,
  `success_count`  INT     NOT NULL DEFAULT 0 COMMENT '非 5xx 请求数',
  `error_count`    INT     NOT NULL DEFAULT 0 COMMENT '5xx 请求数',
  `avg_duration_ms` INT   NOT NULL DEFAULT 0,
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hour_site_agent` (`stat_hour`, `site_id`, `agent_id`),
  KEY `idx_stat_hour` (`stat_hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小时聚合统计';


CREATE TABLE `aworld_stats_daily` (
  `id`            BIGINT   NOT NULL,
  `stat_date`     DATE     NOT NULL COMMENT '统计日期，如 2026-04-24',
  `site_id`       BIGINT            COMMENT '场所 ID（NULL 表示全局）',
  `agent_id`      BIGINT            COMMENT 'Agent ID（NULL 表示不按 Agent 分）',
  `total_requests` INT     NOT NULL DEFAULT 0,
  `success_count`  INT     NOT NULL DEFAULT 0 COMMENT '非 5xx 请求数',
  `error_count`    INT     NOT NULL DEFAULT 0 COMMENT '5xx 请求数',
  `avg_duration_ms` INT   NOT NULL DEFAULT 0,
  `creator` VARCHAR(64) NOT NULL DEFAULT '', `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) NOT NULL DEFAULT '', `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0, `tenant_id` BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_site_agent` (`stat_date`, `site_id`, `agent_id`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='天聚合统计';
