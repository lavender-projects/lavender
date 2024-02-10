const remoteAccessUrlPrefix = import.meta.env.VITE_REMOTE_ACCESS_URL_PREFIX

const lavsourceJsInterfaceMockData = {
  getLocalLavsourceListCanBeAdded: {
    data: [
      {
        id: 1,
        type: 'local',
        name: 'LavSource bilibili',
        packageName: 'de.honoka.lavender.lavsource.bilibili',
        iconUrl: `${remoteAccessUrlPrefix}/mockData/img/lavsource_bilibili.png`
      },
      {
        id: 2,
        type: 'local',
        name: 'test1',
        packageName: 'test1',
        iconUrl: `${remoteAccessUrlPrefix}/mockData/img/lavsource_bilibili.png`
      },
      {
        id: 3,
        type: 'local',
        name: 'test2',
        packageName: 'test2',
        iconUrl: `${remoteAccessUrlPrefix}/mockData/img/lavsource_bilibili.png`
      }
    ]
  },
  getExistingLavsourceList: {
    data: [
      {
        id: 1,
        type: 'local',
        name: 'LavSource bilibili',
        packageName: 'de.honoka.lavender.lavsource.bilibili',
        iconUrl: `${remoteAccessUrlPrefix}/mockData/img/lavsource_bilibili.png`
      },
      {
        id: 2,
        type: 'network',
        name: 'LavSource bilibili Network',
        baseUrl: 'https://lavsource.bilibili.com/api',
        iconUrl: `${remoteAccessUrlPrefix}/mockData/img/lavsource_bilibili.png`
      }
    ]
  }
}

export default lavsourceJsInterfaceMockData