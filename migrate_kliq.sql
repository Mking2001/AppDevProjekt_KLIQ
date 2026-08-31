-- ============================================================
-- KLIQ Cloud SQL PostgreSQL Migration & Schema Script
-- ============================================================

DROP TABLE IF EXISTS "public"."follow" CASCADE;
DROP TABLE IF EXISTS "public"."comment" CASCADE;
DROP TABLE IF EXISTS "public"."like" CASCADE;
DROP TABLE IF EXISTS "public"."post" CASCADE;

-- ===== USER TABLE =====
CREATE TABLE IF NOT EXISTS "public"."user" (
    "id" text NOT NULL,
    "username" text NOT NULL,
    "email" text NOT NULL,
    "first_name" text,
    "last_name" text,
    "birth_date_ms" bigint,
    "age" integer,
    "gender" text NOT NULL DEFAULT 'UNSPECIFIED',
    "hometown" text,
    "country_code" text NOT NULL DEFAULT '+43',
    "phone_number" text,
    "profile_picture_url" text,
    "bio" text,
    "password" text,
    "is_verified" boolean NOT NULL DEFAULT false,
    "updated_at" timestamptz DEFAULT now(),
    "created_at" timestamptz DEFAULT now(),
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."user" 
    ALTER COLUMN "id" TYPE text, 
    ALTER COLUMN "created_at" DROP NOT NULL, 
    ALTER COLUMN "first_name" DROP NOT NULL, 
    ALTER COLUMN "last_name" DROP NOT NULL, 
    ADD COLUMN IF NOT EXISTS "age" integer, 
    ADD COLUMN IF NOT EXISTS "gender" text NOT NULL DEFAULT 'UNSPECIFIED', 
    ADD COLUMN IF NOT EXISTS "hometown" text, 
    ADD COLUMN IF NOT EXISTS "is_verified" boolean NOT NULL DEFAULT false, 
    ADD COLUMN IF NOT EXISTS "profile_picture_url" text, 
    ADD COLUMN IF NOT EXISTS "updated_at" timestamptz;

-- ===== CLUB / VENUE / EVENT-SPOT TABLE =====
CREATE TABLE IF NOT EXISTS "public"."club" (
    "id" text NOT NULL, 
    "name" text NOT NULL, 
    "latitude" double precision NOT NULL DEFAULT 0, 
    "longitude" double precision NOT NULL DEFAULT 0, 
    "address" text NOT NULL DEFAULT '', 
    "geofence_radius_meters" double precision NOT NULL DEFAULT 200, 
    "average_rating" double precision NOT NULL DEFAULT 0, 
    "opening_hours_json" text NOT NULL DEFAULT '', 
    "is_favorite" boolean NOT NULL DEFAULT false, 
    "category" text NOT NULL DEFAULT '', 
    "rating" double precision NOT NULL DEFAULT 0, 
    "image_url" text NOT NULL DEFAULT '', 
    "region" text NOT NULL DEFAULT '', 
    "city" text NOT NULL DEFAULT '', 
    "postal_code" text NOT NULL DEFAULT '', 
    "current_capacity_percent" integer NOT NULL DEFAULT 0, 
    "male_percentage" integer NOT NULL DEFAULT 0, 
    "female_percentage" integer NOT NULL DEFAULT 0, 
    "total_live_visitors" integer NOT NULL DEFAULT 0, 
    "external_search_tags" text NOT NULL DEFAULT '', 
    "website_url" text, 
    "is_promoted" boolean NOT NULL DEFAULT false, 
    "phone_number" text, 
    "contact_email" text, 
    "instagram_handle" text, 
    "flame_count" integer NOT NULL DEFAULT 0, 
    "flame_date" text, 
    PRIMARY KEY ("id")
);

ALTER TABLE "public"."club"
    ADD COLUMN IF NOT EXISTS "flame_count" integer NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS "flame_date" text;

-- ===== CLUB HYPE (FLAMMEN) TABLE =====
CREATE TABLE IF NOT EXISTS "public"."club_hype" (
    "club_id" text NOT NULL,
    "user_id" text NOT NULL,
    "date_string" text NOT NULL,
    "created_at_ms" bigint NOT NULL,
    PRIMARY KEY ("club_id", "user_id", "date_string"),
    CONSTRAINT "club_hype_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "public"."club" ("id") ON DELETE CASCADE,
    CONSTRAINT "club_hype_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== FEED POST TABLE (Öffentlich & Follower-Only Posts, Geotags, Events) =====
CREATE TABLE IF NOT EXISTS "public"."feed_post" (
    "id" text NOT NULL,
    "author_user_id" text NOT NULL,
    "author_name" text NOT NULL,
    "author_avatar_url" text,
    "content_text" text NOT NULL,
    "image_url" text,
    "club_id" text,
    "club_name" text,
    "location_name" text,
    "location_address" text,
    "latitude" double precision,
    "longitude" double precision,
    "is_event_pinned" boolean NOT NULL DEFAULT false,
    "is_followers_only" boolean NOT NULL DEFAULT false,
    "like_count" integer NOT NULL DEFAULT 0,
    "comment_count" integer NOT NULL DEFAULT 0,
    "flame_count" integer NOT NULL DEFAULT 0,
    "flame_date" text,
    "created_at_ms" bigint NOT NULL,
    "created_at" timestamptz DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT "feed_post_author_user_id_fkey" FOREIGN KEY ("author_user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE,
    CONSTRAINT "feed_post_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "public"."club" ("id") ON DELETE SET NULL
);

ALTER TABLE "public"."feed_post"
    ADD COLUMN IF NOT EXISTS "location_address" text,
    ADD COLUMN IF NOT EXISTS "latitude" double precision,
    ADD COLUMN IF NOT EXISTS "longitude" double precision,
    ADD COLUMN IF NOT EXISTS "is_event_pinned" boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS "is_followers_only" boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS "flame_count" integer NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS "flame_date" text;

-- ===== FEED COMMENT TABLE =====
CREATE TABLE IF NOT EXISTS "public"."feed_comment" (
    "id" text NOT NULL,
    "post_id" text NOT NULL,
    "author_user_id" text NOT NULL,
    "author_name" text NOT NULL,
    "text" text NOT NULL,
    "created_at_ms" bigint NOT NULL,
    "created_at" timestamptz DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT "feed_comment_post_id_fkey" FOREIGN KEY ("post_id") REFERENCES "public"."feed_post" ("id") ON DELETE CASCADE,
    CONSTRAINT "feed_comment_author_user_id_fkey" FOREIGN KEY ("author_user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== FEED POST LIKE TABLE =====
CREATE TABLE IF NOT EXISTS "public"."feed_post_like" (
    "post_id" text NOT NULL,
    "user_id" text NOT NULL,
    "created_at_ms" bigint NOT NULL,
    PRIMARY KEY ("post_id", "user_id"),
    CONSTRAINT "feed_post_like_post_id_fkey" FOREIGN KEY ("post_id") REFERENCES "public"."feed_post" ("id") ON DELETE CASCADE,
    CONSTRAINT "feed_post_like_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== STORY TABLE =====
CREATE TABLE IF NOT EXISTS "public"."story" (
    "id" text NOT NULL,
    "author_user_id" text NOT NULL,
    "author_name" text NOT NULL,
    "avatar_url" text,
    "image_url" text NOT NULL,
    "headline" text NOT NULL DEFAULT '',
    "club_name" text,
    "created_at_ms" bigint NOT NULL,
    "created_at" timestamptz DEFAULT now(),
    PRIMARY KEY ("id"),
    CONSTRAINT "story_author_user_id_fkey" FOREIGN KEY ("author_user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== EVENT TABLE =====
CREATE TABLE IF NOT EXISTS "public"."event" (
    "id" text NOT NULL, 
    "club_id" text NOT NULL, 
    "capacity_limit" integer NOT NULL DEFAULT 0, 
    "category" text NOT NULL DEFAULT '', 
    "description" text NOT NULL, 
    "end_time" bigint NOT NULL DEFAULT 0, 
    "image_url" text, 
    "is_cancelled" boolean NOT NULL DEFAULT false, 
    "price" text NOT NULL DEFAULT '', 
    "search_keywords" text NOT NULL DEFAULT '', 
    "special_offers_json" text NOT NULL DEFAULT '', 
    "start_time" bigint NOT NULL DEFAULT 0, 
    "time" text NOT NULL DEFAULT '', 
    "title" text NOT NULL, 
    PRIMARY KEY ("id"), 
    CONSTRAINT "event_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "public"."club" ("id") ON DELETE CASCADE
);

-- ===== CLUB OFFER TABLE =====
CREATE TABLE IF NOT EXISTS "public"."club_offer" (
    "id" text NOT NULL, 
    "club_id" text NOT NULL, 
    "description" text NOT NULL, 
    "discount_code" text, 
    "discount_percentage" integer, 
    "image_url" text, 
    "is_exclusive" boolean NOT NULL DEFAULT false, 
    "offer_type" text NOT NULL, 
    "terms_and_conditions" text, 
    "title" text NOT NULL, 
    "valid_until" bigint, 
    PRIMARY KEY ("id"), 
    CONSTRAINT "club_offer_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "public"."club" ("id") ON DELETE CASCADE
);

-- ===== REVIEW TABLE =====
CREATE TABLE IF NOT EXISTS "public"."review" (
    "id" text NOT NULL, 
    "club_id" text, 
    "event_id" text, 
    "reviewer_user_id" text NOT NULL, 
    "flagged_count" integer NOT NULL DEFAULT 0, 
    "helpful_votes_count" integer NOT NULL DEFAULT 0, 
    "is_verified" boolean NOT NULL DEFAULT false, 
    "rating" integer NOT NULL, 
    "reviewer_avatar_url" text, 
    "reviewer_username" text NOT NULL DEFAULT '', 
    "target_user_id" text, 
    "text" text NOT NULL, 
    "timestamp" bigint NOT NULL, 
    "verification_method" text NOT NULL DEFAULT 'UNVERIFIED', 
    PRIMARY KEY ("id"), 
    CONSTRAINT "review_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "public"."club" ("id") ON DELETE SET NULL, 
    CONSTRAINT "review_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "public"."event" ("id") ON DELETE SET NULL, 
    CONSTRAINT "review_reviewer_user_id_fkey" FOREIGN KEY ("reviewer_user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== CHAT TABLE =====
CREATE TABLE IF NOT EXISTS "public"."chat" (
    "id" text NOT NULL, 
    "avatar_initial" text NOT NULL, 
    "avatar_url" text, 
    "chat_type" text NOT NULL, 
    "city_region" text, 
    "is_archived" boolean NOT NULL DEFAULT false, 
    "is_muted" boolean NOT NULL DEFAULT false, 
    "is_online" boolean NOT NULL DEFAULT false, 
    "is_pinned" boolean NOT NULL DEFAULT false, 
    "last_message_text" text NOT NULL, 
    "last_message_timestamp_iso" text NOT NULL DEFAULT '', 
    "last_message_timestamp_ms" bigint NOT NULL, 
    "last_read_message_id" text, 
    "name" text NOT NULL, 
    "unread_count" integer NOT NULL DEFAULT 0, 
    PRIMARY KEY ("id")
);

-- ===== MESSAGE TABLE =====
CREATE TABLE IF NOT EXISTS "public"."message" (
    "id" text NOT NULL, 
    "chat_id" text NOT NULL, 
    "aspect_ratio" double precision NOT NULL DEFAULT 1, 
    "audio_duration_ms" bigint NOT NULL DEFAULT 0, 
    "caption" text, 
    "delivered_at_ms" bigint, 
    "is_edited" boolean NOT NULL DEFAULT false, 
    "is_mine" boolean NOT NULL, 
    "media_height" integer NOT NULL DEFAULT 0, 
    "media_url" text, 
    "media_width" integer NOT NULL DEFAULT 0, 
    "message_type" text NOT NULL DEFAULT 'TEXT', 
    "read_at_ms" bigint, 
    "reply_to_message_id" text, 
    "sender_name" text NOT NULL, 
    "sender_user_id" text NOT NULL DEFAULT '', 
    "status" text NOT NULL DEFAULT 'SENT', 
    "text" text NOT NULL, 
    "thumbnail_url" text, 
    "timestamp_iso" text NOT NULL DEFAULT '', 
    "timestamp_ms" bigint NOT NULL, 
    PRIMARY KEY ("id"), 
    CONSTRAINT "message_chat_id_fkey" FOREIGN KEY ("chat_id") REFERENCES "public"."chat" ("id") ON DELETE CASCADE
);

-- ===== DIRECT MESSAGE TABLE =====
CREATE TABLE IF NOT EXISTS "public"."direct_message" (
    "id" text NOT NULL, 
    "receiver_id" text NOT NULL, 
    "sender_id" text NOT NULL, 
    "aspect_ratio" double precision NOT NULL DEFAULT 1, 
    "audio_duration_ms" bigint NOT NULL DEFAULT 0, 
    "caption" text, 
    "delivered_at_ms" bigint, 
    "delivery_status" text NOT NULL DEFAULT 'SENT', 
    "encryption_algorithm" text NOT NULL DEFAULT 'AES-256-GCM', 
    "is_encrypted" boolean NOT NULL DEFAULT true, 
    "media_height" integer NOT NULL DEFAULT 0, 
    "media_url" text, 
    "media_width" integer NOT NULL DEFAULT 0, 
    "message_type" text NOT NULL DEFAULT 'TEXT', 
    "read_at_ms" bigint, 
    "text" text NOT NULL, 
    "thumbnail_url" text, 
    "timestamp" bigint NOT NULL, 
    "timestamp_iso" text NOT NULL DEFAULT '', 
    PRIMARY KEY ("id"), 
    CONSTRAINT "direct_message_receiver_id_fkey" FOREIGN KEY ("receiver_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE, 
    CONSTRAINT "direct_message_sender_id_fkey" FOREIGN KEY ("sender_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== FRIEND TABLE =====
CREATE TABLE IF NOT EXISTS "public"."friend" (
    "user_id" text NOT NULL, 
    "friend_user_id" text NOT NULL, 
    "created_at_timestamp_ms" bigint NOT NULL, 
    "is_qr_verified" boolean NOT NULL DEFAULT true, 
    "status" text NOT NULL DEFAULT 'ACCEPTED', 
    PRIMARY KEY ("user_id", "friend_user_id")
);

-- ===== VISITED LOG TABLE =====
CREATE TABLE IF NOT EXISTS "public"."visited_log" (
    "id" text NOT NULL, 
    "club_id" text NOT NULL, 
    "user_id" text NOT NULL, 
    "club_name" text NOT NULL, 
    "is_verified_by_gps" boolean NOT NULL, 
    "visited_at_timestamp" bigint NOT NULL, 
    PRIMARY KEY ("id"), 
    CONSTRAINT "visited_log_club_id_fkey" FOREIGN KEY ("club_id") REFERENCES "public"."club" ("id") ON DELETE CASCADE, 
    CONSTRAINT "visited_log_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== BLOCKED USER TABLE =====
CREATE TABLE IF NOT EXISTS "public"."blocked_user" (
    "user_id" text NOT NULL, 
    "blocked_user_id" text NOT NULL, 
    "blocked_at_timestamp_ms" bigint NOT NULL, 
    "reason" text, 
    PRIMARY KEY ("user_id", "blocked_user_id")
);

-- ===== USER PREFERENCE TABLE =====
CREATE TABLE IF NOT EXISTS "public"."user_preference" (
    "user_id" text NOT NULL, 
    "drinking_habit" text NOT NULL DEFAULT 'NEVER', 
    "is_dark_mode" boolean NOT NULL DEFAULT false, 
    "push_notifications_enabled" boolean NOT NULL DEFAULT true, 
    "search_intent" text NOT NULL DEFAULT 'BOTH', 
    "search_radius_km" integer NOT NULL DEFAULT 10, 
    "smoking_habit" text NOT NULL DEFAULT 'NEVER', 
    PRIMARY KEY ("user_id"), 
    CONSTRAINT "user_preference_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "public"."user" ("id") ON DELETE CASCADE
);

-- ===== USER LOCATION TABLE =====
CREATE TABLE IF NOT EXISTS "public"."user_location" (
    "id" text NOT NULL DEFAULT gen_random_uuid()::text, 
    "accuracy" double precision NOT NULL, 
    "latitude" double precision NOT NULL, 
    "longitude" double precision NOT NULL, 
    "speed" double precision NOT NULL DEFAULT 0, 
    "timestamp_ms" bigint NOT NULL, 
    PRIMARY KEY ("id")
);
