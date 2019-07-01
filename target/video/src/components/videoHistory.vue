<template>
    <div>
        <el-collapse accordion @change="activeChange">
            <el-collapse-item v-for="record in videoRecords" :key="record.id" :name="record.id">
                <span class="title" slot="title">{{record.videoName}}</span>
                <el-card>
                    <div v-for="url in record.urls" :key="url" class="imgDiv">
                        <span @click="showOrigin(url)"><el-image :src="url" :fit="'contain'"></el-image></span>
                    </div>
                </el-card>
            </el-collapse-item>
        </el-collapse>
        <el-dialog :title="shotImgTitle" :visible.sync="shotImgVisible" :closed="dialogClosed">
            <el-image :src="shotImgUrl" :fit="'contain'"></el-image>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "videoHistory",
        data() {
            return {
                videoRecords: [],
                shotImgTitle: '',
                shotImgUrl:'',
                shotImgVisible: false
            }
        },
        created() {
            this.getLiveRecord();
        },
        methods: {
            activeChange(index) {
                this.getShotImg(index);
            },
            getLiveRecord() {
                this.$axios.get(location.origin.concat('/api/liveRecords')).then(resp => {
                    let r = resp.data;
                    for(let i = 0; i < r.length; i++) {
                        this.videoRecords.push({id: i, videoId: r[i].videoId, videoName: r[i].videoName, createTime: r[i].createTime, urls: []});
                    }
                }).catch(err =>{
                    this.$message.error('获取直播历史失败');
                    console.log(err);
                });
            },
            getShotImg(index) {
                let videoId = this.videoRecords[index].videoId;
                this.$axios.get(location.origin.concat('/api/shotImg/',videoId)).then(resp => {
                    let r = resp.data;
                    for(let i = 0; i < r.length; i++) {
                        let url = location.origin.concat('/api/downShotImg/',videoId,'?imgName=',encodeURIComponent(r[i]));
                        if(this.videoRecords[index].urls.indexOf(url) < 0) {
                            this.videoRecords[index].urls.push(url);
                        }
                    }
                }).catch(err =>{
                    this.$message.error('获取直播截图失败');
                    console.log(err);
                });
            },
            showOrigin(url) {
                this.shotImgUrl = url;
                this.shotImgVisible = true;
            },
            dialogClosed() {
                this.shotImgVisible = false;
            }
        }
    }
</script>

<style scoped>
    .imgDiv {
        width: 200px;
        display: inline-block;
        padding-left: 2px;
        padding-right: 2px;
    }
    .title {
        margin-left: 3rem;
        font-size: 1rem;
    }
</style>