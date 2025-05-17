ALTER TABLE broadcast_slot
    ADD CONSTRAINT unique_broadcast_slot UNIQUE (start_time, end_time, radio_station_id);