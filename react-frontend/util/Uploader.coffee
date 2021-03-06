class Uploader

  constructor: (@url) ->

  upload: (
    data
    contentType
    successCallback = () ->
    ) ->
    $.ajax
      url: @url
      type: 'POST'
      data: data
      dataType: 'json'
      cache: if contentType is false then false else true
      contentType: contentType
      processData: if contentType is false then false else true
      xhr: () ->
        xhr = new window.XMLHttpRequest()
        xhr.upload.addEventListener 'progress' , (evt) ->
          if evt.lengthComputable
            percentComplete = evt.loaded / evt.total
            console.log(percentComplete)
        , false
        xhr
      success: (data) ->
        if data.error
          alert data.error
        else
          successCallback()
      error: (jqXHR, status, error) ->
        console.log('Error: '  + error + '\n' + 'Status: ' + status)

module.exports = Uploader
