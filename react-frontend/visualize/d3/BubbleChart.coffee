d3 = require 'd3'
Colors = require 'material-ui/lib/styles/colors'
Helper = require '../../util/Helper'

class BubbleChart
  constructor: (data) ->
    @data = data
    @nodes = []
    @elements = null
    @svg = null
    @width = 600
    @height = 600
    @center = {x: @width / 2, y: @height / 2}
    @damper = 0.1

  charge: (d) ->
    -Math.pow(d.radius, 2) / 8

  setupVisualization: () ->
    @svg = d3.select('#charts-box').append('svg')
        .attr('width', @width)
        .attr('height', @height)

    @elements = @svg.selectAll('g').data(@nodes).enter()
      .append('g')
      .on('mouseover', (d) -> console.log(d))

    @elements.append('circle')
      .attr('r', (d) -> d.radius)
      .attr('fill', 'white')
      .attr('stroke-width', 1)
      .attr('stroke', 'black')

    @elements.append('text')
      .attr('text-anchor', 'middle')
      .attr('dominant-baseline', 'central')
	    .text((d) -> Helper.getInitials(d.name))

  initNodes: () ->
    @nodes = @data.map (a) ->
      {
        id: a.id
        radius: 20 + a.trackCount * a.score * 6
        trackCount: a.trackCount
        score: a.score
        name: a.name
      }

  move_towards_center: (alpha) =>
    (d) =>
      d.x = d.x + (@center.x - d.x) * (@damper + 0.02) * alpha
      d.y = d.y + (@center.y - d.y) * (@damper + 0.02) * alpha

  start: () ->
    d3.select('#charts-box').select('svg').remove()
    @nodes = []
    @initNodes()
    @setupVisualization()
    @force = d3.layout.force()
      .size([@width, @height])
      .gravity(-0.01)
      .nodes(@nodes)
      .charge(@charge)
      .friction(0.9)
      .on 'tick', (e) =>
        @elements.each(@move_towards_center(e.alpha))
          .attr('transform', (d) -> 'translate('+d.x+','+d.y+')')

    @force.start()



module.exports = BubbleChart