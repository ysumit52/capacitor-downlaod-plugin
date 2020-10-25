import { WebPlugin } from '@capacitor/core';
import { DownloadPlugin } from './definitions';

export class DownloadPluginWeb extends WebPlugin implements DownloadPlugin {
  constructor() {
    super({
      name: 'DownloadPlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const DownloadPlugin = new DownloadPluginWeb();

export { DownloadPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(DownloadPlugin);
