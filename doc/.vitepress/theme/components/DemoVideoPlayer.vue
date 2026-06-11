<template>
  <section class="ic-demo-video">
    <div class="ic-demo-video__header">
      <div>
        <span class="ic-demo-video__eyebrow">{{ eyebrow }}</span>
        <h2>{{ title }}</h2>
      </div>
      <p>{{ description }}</p>
    </div>

    <div class="ic-demo-video__frame">
      <video ref="videoRef" controls playsinline preload="metadata"></video>
      <div v-if="statusText" class="ic-demo-video__status">{{ statusText }}</div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue';

const props = withDefaults(
  defineProps<{
    src: string;
    eyebrow?: string;
    title?: string;
    description?: string;
  }>(),
  {
    eyebrow: '案例展示',
    title: 'IcHotel PMS 数字孪生酒店运营案例',
    description: '基于 IC Framework 构建的酒店 PMS，覆盖数字孪生看板、运营中枢与多端业务协同。',
  },
);

const videoRef = ref<HTMLVideoElement | null>(null);
const statusText = ref('');
let hls: import('hls.js').default | null = null;

onMounted(async () => {
  const video = videoRef.value;
  if (!video) return;

  if (video.canPlayType('application/vnd.apple.mpegurl')) {
    video.src = props.src;
    return;
  }

  try {
    const { default: Hls } = await import('hls.js');
    if (!Hls.isSupported()) {
      statusText.value = '当前浏览器不支持 HLS 播放，请复制地址到播放器中打开';
      return;
    }

    hls = new Hls();
    hls.loadSource(props.src);
    hls.attachMedia(video);
    hls.on(Hls.Events.ERROR, (_event, data) => {
      if (data.fatal) {
        statusText.value = '视频加载失败，请检查 m3u8 地址或跨域配置';
      }
    });
  } catch {
    statusText.value = '播放器加载失败，请复制地址到播放器中打开';
  }
});

onBeforeUnmount(() => {
  hls?.destroy();
  hls = null;
});
</script>
