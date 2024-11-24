from sqlalchemy import (
    Column,
    Integer,
    String,
    Text,
    Boolean,
    Float,
    ForeignKey,
    DateTime,
    PrimaryKeyConstraint,
)
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.sql import func

Base = declarative_base()


class Config(Base):
    __tablename__ = "config"
    key = Column(Text, primary_key=True)
    value = Column(Text, nullable=False)
    type = Column(Text, nullable=False)


class Users(Base):
    __tablename__ = "users"
    discord_user_id = Column(String(20), primary_key=True)
    blacklisted = Column(Boolean, default=False)
    permissions = Column(Text, default=None)


class ChannelMessageTraffic(Base):
    __tablename__ = "channel_message_traffic"
    time_stamp = Column(DateTime, primary_key=True, default=func.current_timestamp())
    eth_place_bots = Column(Integer)
    count_thread = Column(Integer)


class PlaceThroughputLog(Base):
    __tablename__ = "place_throughput_log"
    time_stamp = Column(DateTime, primary_key=True, default=func.current_timestamp())
    batch_size = Column(Integer, default=3600)
    message_batch_time = Column(Integer)


class MessageDeleteTracker(Base):
    __tablename__ = "message_delete_tracker"
    discord_message_id = Column(String(20), primary_key=True)
    discord_server_id = Column(String(20))
    discord_channel_id = Column(String(20))
    time_to_delete = Column(DateTime)


class PlaceProjects(Base):
    __tablename__ = "place_projects"
    project_id = Column(Integer, primary_key=True, autoincrement=True)
    pixels_drawn = Column(Integer, default=0)
    discord_user_id = Column(String(20))


class PlacePixels(Base):
    __tablename__ = "place_pixels"
    project_id = Column(Integer, ForeignKey("place_projects.project_id", ondelete="CASCADE"), primary_key=True)
    index = Column(Integer, primary_key=True)
    x_coordinate = Column(Integer, nullable=False)
    y_coordinate = Column(Integer, nullable=False)
    image_color = Column(String(6))
    alpha = Column(Float, default=1.0)
    place_color = Column(String(6))


class AccessControl(Base):
    __tablename__ = "access_control"
    discord_server_id = Column(String(20), primary_key=True)
    discord_channel_ids = Column(Text, default=None)
