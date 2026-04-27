import { resolve } from 'path'
import Vue from '@vitejs/plugin-vue'
import VueJsx from '@vitejs/plugin-vue-jsx'
import ElementPlus from 'unplugin-element-plus/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export function createVitePlugins() {
  const root = process.cwd()

  // 路径查找
  function pathResolve(dir: string) {
    return resolve(root, '.', dir)
  }

  return [
    Vue(),
    VueJsx(),
    ElementPlus({}),
    AutoImport({
      include: [
        /\.[tj]sx?$/, // .ts, .tsx, .js, .jsx
        /\.vue$/,
        /\.vue\?vue/, // .vue
        /\.md$/ // .md
      ],
      imports: [
        'vue',
        'vue-router'
      ],
      dts: 'src/types/auto-imports.d.ts',
      resolvers: [ElementPlusResolver()],
      eslintrc: {
        enabled: false,
        filepath: './.eslintrc-auto-import.json',
        globalsPropValue: true
      }
    }),
    Components({
      dts: 'src/types/auto-components.d.ts',
      resolvers: [ElementPlusResolver()],
      globs: ["src/components/**/**.{vue}"]
    })
  ]
}
