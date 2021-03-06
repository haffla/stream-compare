React = require 'react'
_ = require 'lodash'

Colors = require 'material-ui/lib/styles/colors'
FontIcon = require 'material-ui/lib/font-icon'
List = require 'material-ui/lib/lists/list'
ListItem = require 'material-ui/lib/lists/list-item'

RightView = React.createClass

  render: () ->
    selectedAlbums = @props.selectedAlbums.map (album, idx) =>
      icon = if album.inCollection then "check_box" else "check_box_outline_blank"
      color = if album.name == @props.selectedAlbum then Colors.amber500 else 'white'
      <ListItem
        key={idx}
        style={backgroundColor: color}
        onTouchTap={@props.handleAlbumClick.bind(null, idx)}
        primaryText={album.name}
        rightAvatar={<FontIcon color="#455a64" className="material-icons" >{icon}</FontIcon>}
        />
    <div style={width: '33%'}>
      <List subheader={@props.artist + "'s " + "Albums on " + @props.name}>
        {selectedAlbums}
      </List>
    </div>

module.exports = RightView
