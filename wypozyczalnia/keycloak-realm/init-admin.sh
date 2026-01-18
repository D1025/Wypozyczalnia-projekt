#!/bin/sh
set -eux

echo "[keycloak-init] start"

echo "[keycloak-init] waiting for Keycloak admin API to be ready"
# kcadm will fail fast until Keycloak is ready; loop until it can log in.
until /opt/keycloak/bin/kcadm.sh config credentials \
  --server "${KEYCLOAK_URL}" \
  --realm master \
  --user "${KEYCLOAK_ADMIN}" \
  --password "${KEYCLOAK_ADMIN_PASSWORD}" >/dev/null 2>&1; do
  sleep 2
done

echo "[keycloak-init] Keycloak is ready"

echo "[keycloak-init] ensuring user ${APP_ADMIN_USERNAME} in realm ${KEYCLOAK_REALM}"

USER_ID=$( /opt/keycloak/bin/kcadm.sh get users -r "${KEYCLOAK_REALM}" -q username="${APP_ADMIN_USERNAME}" \
  | sed -n 's/.*"id"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' \
  | head -n 1 )

if [ -z "$USER_ID" ]; then
  echo "[keycloak-init] creating user ${APP_ADMIN_USERNAME}"
  /opt/keycloak/bin/kcadm.sh create users \
    -r "${KEYCLOAK_REALM}" \
    -s username="${APP_ADMIN_USERNAME}" \
    -s enabled=true \
    -s email="${APP_ADMIN_EMAIL}" \
    -s emailVerified=true

  USER_ID=$( /opt/keycloak/bin/kcadm.sh get users -r "${KEYCLOAK_REALM}" -q username="${APP_ADMIN_USERNAME}" \
    | sed -n 's/.*"id"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' \
    | head -n 1 )
fi

echo "[keycloak-init] userId=${USER_ID}"
if [ -z "$USER_ID" ]; then
  echo "[keycloak-init] ERROR: could not resolve user id for ${APP_ADMIN_USERNAME}" >&2
  exit 1
fi

/opt/keycloak/bin/kcadm.sh set-password \
  -r "${KEYCLOAK_REALM}" \
  --userid "${USER_ID}" \
  --new-password "${APP_ADMIN_PASSWORD}" \
  --temporary=false

CLIENT_UUID=$( /opt/keycloak/bin/kcadm.sh get clients -r "${KEYCLOAK_REALM}" -q clientId="${KEYCLOAK_APP_CLIENT_ID}" \
  | sed -n 's/.*"id"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' \
  | head -n 1 )

if [ -n "$CLIENT_UUID" ]; then
  echo "[keycloak-init] assigning role ${APP_ADMIN_ROLE} on client ${KEYCLOAK_APP_CLIENT_ID}"
  /opt/keycloak/bin/kcadm.sh add-roles \
    -r "${KEYCLOAK_REALM}" \
    --uusername "${APP_ADMIN_USERNAME}" \
    --cclientid "${KEYCLOAK_APP_CLIENT_ID}" \
    --rolename "${APP_ADMIN_ROLE}"
else
  echo "[keycloak-init] WARNING: client ${KEYCLOAK_APP_CLIENT_ID} not found in realm ${KEYCLOAK_REALM}"
fi

echo "[keycloak-init] done"
