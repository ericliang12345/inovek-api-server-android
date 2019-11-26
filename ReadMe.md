API
This API is a RESTful APIs back-end Android project.

# Support OS
Android 6.0

# Simulator
- Nexus 4API 23 - Android 6.0
- Nexus 7 API 23 - Android 6.0


# Code Structure

- app
  - java
    - bms.device.webapi
      - api
        - app:   android apps information
        - audio: control device's audio volume
        - bt:    control device's bluetooth 
        - info:  device's information
        - led:   control platform's leds
        - net:   platform's network
        - nfc:   read nfc module
        - prop:  property of system ( not completed )
        - task:  log ( not completed )
        - wifi:  wifi management
        - ApiOAuth2Token: Authorization
        - ApiUserPassword
      - user: user account manager
    - ManiActivity.java: Main
    - WebServer.java: WebServer


