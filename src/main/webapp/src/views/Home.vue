<template>
    <div>
        <vHeader/>
        <router-view/>
    </div>
</template>

<script>
    import vHeader from '@/components/header.vue'
    export default {
        name: 'home',
        components: {
            vHeader
        }
    }
</script>
