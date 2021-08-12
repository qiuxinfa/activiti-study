import request from '@/utils/request'
const prefix = '/workflow'

export function addModel(params) {
  return request({
    url: prefix + '/models',
    method: 'post',
    params
  })
}

export function getModels() {
  return request({
    url: prefix + '/models',
    method: 'get'
  })
}

export function saveModelXml(data) {
  return request({
    url: prefix + '/saveModelXml/',
	method: 'post',
    data
  })
}

export function deleteModel(modelId) {
  return request({
    url: prefix + '/models/' + modelId,
    method: 'delete'
  })
}

export function getEditorXml(modelId) {
  return request({
    url: prefix + '/models/' + modelId,
    method: 'get'
  })
}

export function deployModel(modelId) {
  return request({
    url: prefix + '/models/deploy/' + modelId,
    method: 'post'
  })
}