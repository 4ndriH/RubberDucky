FROM python:3.11-slim

WORKDIR /app/postgres

COPY . /app/postgres

RUN ls -la

RUN pip install --no-cache-dir -r alembic_requirements.txt

# Run all Alembic migrations at container startup
CMD ["alembic", "upgrade", "head"]
