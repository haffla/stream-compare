_ = require 'lodash'
Helper = require '../util/Helper'

React = require 'react'
BubbleChart = require './d3/BubbleChart'
Colors = require 'material-ui/lib/styles/colors'
BarChart = require './d3/BarChart'
ServiceChart = require './d3/ServiceChart'
BarChartSimple = require './d3/BarChartSimple'

FlatButton = require 'material-ui/lib/flat-button'

ChartsBox = React.createClass

  barChartSimple: null
  serviceChart: null

  getInitialState: () ->
    data: {user: {}}, selectedArtist: {}

  handleBubbleClick: (d) ->
    @setState selectedArtist: d
    id = d.id
    $('.artist').attr('fill', Colors.amber700)
    $('.artist' + id).attr('fill', Colors.purple600)
    @barChartSimple.makeBarChart [@state.data.spotify[id], @state.data.deezer[id], @state.data.napster[id]]

  componentDidMount: () ->
    $.ajax '/vidata',
      type: 'GET',
      dataType: 'json',
      success: (data) =>
        console.log(data)
        @setState data: data, totalAlbumCount: Helper.calculateNrOfServiceAlbums(data.total)
        bubble = new BubbleChart(@state.data, @handleBubbleClick)
        bar = new BarChart(@state.data.user)
        @barChartSimple = new BarChartSimple '#simple-chartbox', '.simple-chart'
        @serviceChart = new ServiceChart({
          artists: @state.data.user
          totals: @state.data.total
          })
        missingAlbumChart = new BarChartSimple '#missing-album-chartbox', '.missing-albumchart'
        missingAlbumChart.makeBarChart(
          [@state.data.missing.spotify, @state.data.missing.deezer, @state.data.missing.napster]
          )
        bar.start()
        bubble.start()
        @serviceChart.start()
      error: (e) ->
        console.log(e)

  boxStyle: {
    width: '49%'
    height: 500
    backgroundColor: Colors.grey300
    position: 'relative'
    borderRadius: 5
  }

  dividedBoxStyle: {
    width: '49%'
    height: 500
    display: 'flex'
    flexDirection: 'column'
  }

  innerBoxStyle: {
    width: '100%'
    height: '25%'
    padding: '40px 20px'
    position: 'relative'
    backgroundColor: Colors.grey300
    borderRadius: 5
    marginBottom: 20
  }

  handleServiceButtonClick: (target) ->
    data = switch target
      when 'spotify' then @state.data.spotify
      when 'deezer' then @state.data.deezer
      when 'napster' then @state.data.napster
      else @state.data.total
    @setState totalAlbumCount: Helper.calculateNrOfServiceAlbums data
    @serviceChart.redraw data
  render: () ->
    boxDescriptionStyle = {padding: 8, color: Colors.grey500, position: 'absolute', right: 0, top: 0}
    <div>
      <div style={display: 'flex', justifyContent: 'space-between'}>
        <div id='charts-box-left' style={@boxStyle}>
          <span style={boxDescriptionStyle}>{"Your top #{this.state.data.user.length} artists"}</span>
          <svg className='bubble-chart'></svg>
        </div>
        <div id='charts-box-right' style={@dividedBoxStyle}>
          <div id='simple-chartbox' style={@innerBoxStyle}>
            {
              displayChart = if _.isEmpty(@state.selectedArtist) then 'none' else 'block'
              displayNote = if displayChart is 'block' then 'none' else 'block'
              <div style={display: 'relative'}>
                <div style={display: displayChart}>
                  <span style={boxDescriptionStyle}>{"Number of tracks in your collection per artist"}</span>
                  <svg className='simple-chart'></svg>
                </div>
                <span style={display: displayNote, left: 10, top: 10, position: 'absolute', color: Colors.yellow800}>{"Select an artist to display this chart"}</span>
              </div>
            }
          </div>
          <div id='missing-album-chartbox' style={@innerBoxStyle}>
            <span style={boxDescriptionStyle}>{"Number of missing albums per service"}</span>
            <svg className='missing-albumchart'></svg>
          </div>
        </div>
      </div>

      <div style={display: 'flex', justifyContent: 'space-between', marginTop: 20}>
        <div style={@boxStyle}>
          <div style={height: 20}>
            <FlatButton backgroundColor={Colors.grey300} onClick={@handleServiceButtonClick.bind(null, 'all')} label='All' />
            <FlatButton backgroundColor={Colors.grey300} onClick={@handleServiceButtonClick.bind(null, 'spotify')} label='Spotify' secondary={true} />
            <FlatButton backgroundColor={Colors.grey300} onClick={@handleServiceButtonClick.bind(null, 'deezer')} label='Deezer' secondary={true} />
            <FlatButton backgroundColor={Colors.grey300} onClick={@handleServiceButtonClick.bind(null, 'napster')} label='Napster' secondary={true} />
          </div>
          <svg id='service-chart'></svg>
          <span style={boxDescriptionStyle}>{"Number of albums per service and artist: " + @state.totalAlbumCount}</span>
        </div>
        <div style={@boxStyle}>
          {
            if !_.isEmpty(@state.data.user)
              nrTotalTracks = @state.data.user.reduce (x,y) ->
                  x + y.trackCount
                , 0
          }
          <span style={boxDescriptionStyle}>{"Number of tracks in your collection per artist: " + nrTotalTracks || 0}</span>
          <div id='trackcount-chartbox' style={width: '100%', height: '100%', marginTop: 27}>
            <svg className='trackcount-chart'></svg>
          </div>
        </div>
      </div>
    </div>


module.exports = ChartsBox
