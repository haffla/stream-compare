React = require 'react'
StreamingServiceBox = require './StreamingServiceBox'
SpotifyHelper = require '../util/SpotifyHelper'
DeezerHelper = require '../util/DeezerHelper'
NapsterHelper = require '../util/NapsterHelper'

Colors = require 'material-ui/lib/styles/colors'
Tabs = require 'material-ui/lib/tabs/tabs'
Tab = require 'material-ui/lib/tabs/tab'

AnalysisBox = React.createClass

  tabStyle: {width: '90%', maxWidth: 1700, margin: 'auto', marginTop: 16}

  render: () ->
    <Tabs>
      <Tab label="Spotify">
        <div style=@tabStyle>
          <StreamingServiceBox
            name="Spotify"
            showPlayer={true}
            artistEndpoint="/spotify/artists"
            artistDetailEndpoint="/spotify/artist-detail"
            albumDetailEndpoint="/spotify/album-detail"
            helper={SpotifyHelper} />
        </div>
      </Tab>
      <Tab label="Deezer">
        <div style=@tabStyle>
          <StreamingServiceBox
            name="Deezer"
            showPlayer={false}
            artistEndpoint="/deezer/artists"
            artistDetailEndpoint="/deezer/artist-detail"
            albumDetailEndpoint="/deezer/album-detail"
            helper={DeezerHelper} />
        </div>
      </Tab>
      <Tab label="Napster">
        <div style=@tabStyle>
          <StreamingServiceBox
            name="Napster"
            showPlayer={false}
            artistEndpoint="/napster/artists"
            artistDetailEndpoint="/napster/artist-detail"
            albumDetailEndpoint="/napster/album-detail"
            helper={NapsterHelper} />
        </div>
      </Tab>
    </Tabs>

module.exports = AnalysisBox
