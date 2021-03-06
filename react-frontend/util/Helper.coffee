_ = require 'lodash'

class Helper
  @calculateNrOfAlbums: (data) ->
    _.values(data).reduce (x,y) ->
      x + y.albums.length
    , 0

  @calculateNrOfServiceAlbums: (data) ->
    _.values(data).reduce (x,y) ->
      x+y
    , 0

  @isMac: () ->
    if navigator.platform.match(/(Mac|iPhone|iPod|iPad)/i) then true else false

  @isWindows: () ->
    if navigator.platform.match(/Win/i) then true else false

  @preventDef: (event) ->
    event.stopPropagation()
    event.preventDefault()

  @getInitials: (name) ->
    arr = (name.split(' ').map (s) -> s[0].toUpperCase())
    if arr.length > 2 then _.first(arr).concat(_.last(arr)) else arr.join('')

  @getTrackCountProse: (trackCount) ->
    track = " Track"
    if trackCount > 1
      track = track + "s"
    trackCount + track + " in total"

module.exports = Helper
