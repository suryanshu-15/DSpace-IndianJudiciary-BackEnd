--
-- The contents of this file are subject to the license and copyright
-- detailed in the LICENSE and NOTICE files at the root of the source
-- tree and available online at
--
-- http://www.dspace.org/license/
--

-------------------------------------------------------------------------------
-- Create custom tables for Indian Judiciary DSpace system (Diracai)
-- These tables support audit logging, bulk uploads, file versioning, etc.
-------------------------------------------------------------------------------

-- Table 1: file_hash_record
CREATE TABLE IF NOT EXISTS file_hash_record (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    hash_value VARCHAR(255),
    created_at TIMESTAMP,
    ack_id VARCHAR(255),
    zip_status VARCHAR(255),
    post_response TEXT,
    post_status VARCHAR(255),
    get_check_response TEXT,
    get_check_status VARCHAR(255),
    file_count INTEGER
);

-- Table 2: bitstream_comment
-- Drop if exists and recreate to ensure proper ownership
DROP TABLE IF EXISTS bitstream_comment CASCADE;
CREATE TABLE bitstream_comment (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    bitstream_id UUID NOT NULL,
    commenter_id UUID NOT NULL,
    comment_date TIMESTAMP NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_bitstream_comment_bitstream_id ON bitstream_comment(bitstream_id);
CREATE INDEX idx_bitstream_comment_commenter_id ON bitstream_comment(commenter_id);

-- Table 3: bulk_upload_request
CREATE TABLE IF NOT EXISTS bulk_upload_request (
    bulk_upload_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    uploader_id UUID NOT NULL,
    collection_id UUID NOT NULL,
    reviewer_id UUID NOT NULL,
    filename VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    uploaded_date TIMESTAMP NOT NULL,
    reviewed_date TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_bulk_upload_request_uploader ON bulk_upload_request(uploader_id);
CREATE INDEX IF NOT EXISTS idx_bulk_upload_request_reviewer ON bulk_upload_request(reviewer_id);
CREATE INDEX IF NOT EXISTS idx_bulk_upload_request_status ON bulk_upload_request(status);

-- Table 4: bulk_upload_item
CREATE TABLE IF NOT EXISTS bulk_upload_item (
    bulk_upload_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    upload_request_id UUID NOT NULL,
    item_folder VARCHAR(500) NOT NULL,
    FOREIGN KEY (upload_request_id) REFERENCES bulk_upload_request(bulk_upload_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bulk_upload_item_request ON bulk_upload_item(upload_request_id);

-- Table 5: bulk_upload_metadata
CREATE TABLE IF NOT EXISTS bulk_upload_metadata (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL,
    metadata_key VARCHAR(255) NOT NULL,
    metadata_value TEXT NOT NULL,
    FOREIGN KEY (item_id) REFERENCES bulk_upload_item(bulk_upload_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_bulk_upload_metadata_item ON bulk_upload_metadata(item_id);
CREATE INDEX IF NOT EXISTS idx_bulk_upload_metadata_key ON bulk_upload_metadata(metadata_key);

-- Table 6: file_access_log
CREATE SEQUENCE IF NOT EXISTS file_access_log_id_seq;

CREATE TABLE IF NOT EXISTS file_access_log (
    id BIGINT PRIMARY KEY DEFAULT nextval('file_access_log_id_seq'),
    file_name VARCHAR(500),
    action VARCHAR(100),
    user_id UUID,
    file_id UUID,
    user_email VARCHAR(255),
    ip_address VARCHAR(50),
    user_agent TEXT,
    timestamp TIMESTAMP,
    suspicious BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_file_access_log_user ON file_access_log(user_id);
CREATE INDEX IF NOT EXISTS idx_file_access_log_file ON file_access_log(file_id);
CREATE INDEX IF NOT EXISTS idx_file_access_log_timestamp ON file_access_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_file_access_log_suspicious ON file_access_log(suspicious);

-- Table 7: file_version_history
CREATE TABLE IF NOT EXISTS file_version_history (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(500),
    version_id VARCHAR(100),
    modified_by VARCHAR(255),
    comment TEXT,
    file_content BYTEA,
    modified_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_file_version_history_filename ON file_version_history(file_name);
CREATE INDEX IF NOT EXISTS idx_file_version_history_version ON file_version_history(version_id);

-- Table 8: login_device_audit
CREATE TABLE IF NOT EXISTS login_device_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eperson_id UUID NOT NULL,
    ip_address VARCHAR(50),
    user_agent TEXT,
    device_id VARCHAR(255),
    login_time TIMESTAMP,
    status VARCHAR(50),
    failed_attempts INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_login_device_audit_eperson ON login_device_audit(eperson_id);
CREATE INDEX IF NOT EXISTS idx_login_device_audit_login_time ON login_device_audit(login_time);
CREATE INDEX IF NOT EXISTS idx_login_device_audit_status ON login_device_audit(status);

-- Table 9: role_audit_log
CREATE TABLE IF NOT EXISTS role_audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acted_by UUID,
    affected_user UUID,
    action VARCHAR(255),
    target VARCHAR(500),
    timestamp TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent VARCHAR(512)
);

CREATE INDEX IF NOT EXISTS idx_role_audit_log_acted_by ON role_audit_log(acted_by);
CREATE INDEX IF NOT EXISTS idx_role_audit_log_affected_user ON role_audit_log(affected_user);
CREATE INDEX IF NOT EXISTS idx_role_audit_log_timestamp ON role_audit_log(timestamp);

-- Table 10: user_session_audit
CREATE TABLE IF NOT EXISTS user_session_audit (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    email VARCHAR(255),
    ip_address VARCHAR(50),
    user_agent TEXT,
    event_type VARCHAR(50),
    timestamp TIMESTAMP,
    session_id VARCHAR(255),
    login_time TIMESTAMP,
    logout_time TIMESTAMP,
    duration_seconds BIGINT
);

CREATE INDEX IF NOT EXISTS idx_user_session_audit_user ON user_session_audit(user_id);
CREATE INDEX IF NOT EXISTS idx_user_session_audit_session ON user_session_audit(session_id);
CREATE INDEX IF NOT EXISTS idx_user_session_audit_event ON user_session_audit(event_type);
CREATE INDEX IF NOT EXISTS idx_user_session_audit_timestamp ON user_session_audit(timestamp);